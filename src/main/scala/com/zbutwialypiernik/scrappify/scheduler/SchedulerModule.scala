package com.zbutwialypiernik.scrappify.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.helper.{OneTimeTask, RecurringTaskWithPersistentSchedule, Tasks}
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.config.{DatabaseConfiguration, SchedulerConfiguration}
import com.zbutwialypiernik.scrappify.scrapper.ScrappingTask
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import io.lemonlabs.uri.AbsoluteUrl
import org.postgresql.ds.PGSimpleDataSource

import javax.sql.DataSource
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

class SchedulerModule(databaseConfiguration: DatabaseConfiguration,
                      schedulerConfiguration: SchedulerConfiguration,
                      scrappingTask: ScrappingTask)(implicit val executionContext: ExecutionContext) extends StrictLogging {

  lazy val siteProductScheduler: SiteProductScheduler = wire[DbSiteProductScheduler]

  private val recurringTask: RecurringTaskWithPersistentSchedule[ScheduleAndString] = Tasks.recurringWithPersistentSchedule("cron-site-product-scrapper", classOf[ScheduleAndString])
    .execute((instance, _) => executeTask(instance.getId.toInt, instance.getData.url))

  private val oneTimeTask: OneTimeTask[String] = Tasks.oneTime("onetime-site-product-scrapper", classOf[String])
    .execute((instance, _) => executeTask(instance.getId.toInt, AbsoluteUrl.parse(instance.getData)))

  private def executeTask(productId: Int, url: AbsoluteUrl): Unit = {
    logger.info(s"Starting a new scrapping task for product $productId at $url")

    Await.result(scrappingTask.execute(productId, url)
      .value
      .andThen {
        case Success(Left(error: ServiceError)) => logger.error(s"Service error in scheduled scrapping task for product $productId at $url\nmessage: ${error.message}")
        case Success(Right(result: SiteProductSnapshot)) => logger.info(s"Fetched new data for product $productId at $url, $result")
        case Failure(exception) => logger.error(s"Uncaught exception in scheduled scrapping task for product $productId", exception)
      }, Duration.Inf)
  }

  private val dataSource: DataSource = {
    val dataSource = new PGSimpleDataSource()
    dataSource.setURL(databaseConfiguration.url)
    dataSource.setDatabaseName(databaseConfiguration.name)
    dataSource.setUser(databaseConfiguration.user)
    dataSource.setPassword(databaseConfiguration.password)

    dataSource
  }

  private val scheduler: Scheduler = {
    Scheduler.create(dataSource, recurringTask, oneTimeTask)
      .threads(schedulerConfiguration.threads)
      .enableImmediateExecution()
      .registerShutdownHook()
      .build()
  }

  def init(): Unit = {
    scheduler.start()
  }

}
