package com.zbutwialypiernik.scrappify.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.helper.{RecurringTaskWithPersistentSchedule, Tasks}
import com.softwaremill.macwire.wire
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.config.{DatabaseConfiguration, SchedulerConfiguration}
import com.zbutwialypiernik.scrappify.scrapper.{ScrappingResult, ScrappingTask}
import org.postgresql.ds.PGSimpleDataSource

import javax.sql.DataSource
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

class SchedulerModule(val databaseConfiguration: DatabaseConfiguration,
                      val schedulerConfiguration: SchedulerConfiguration,
                      val scrappingTask: ScrappingTask)(implicit executionContext: ExecutionContext) extends StrictLogging {

  lazy val dbSiteProductScheduler = wire[DbSiteProductScheduler]

   val task: RecurringTaskWithPersistentSchedule[ScheduleAndInt] = Tasks.recurringWithPersistentSchedule("site-product-scrapper", classOf[ScheduleAndInt])
    .execute((instance, _) => {
      Await.result(scrappingTask.execute(instance.getData.productId)
        .value
        .andThen {
          case Success(value) => value match {
            case Left(error) => logger.error(s"Service error in ${instance.getTaskName}#${instance.getId} for product ${instance.getData.productId}", error.message)
            case Right(result) => logger.info(s"Fetched new data for product ${instance.getData.productId}, $result")
          }
          case Failure(exception) => logger.error(s"Uncaught exception in ${instance.getTaskName}#${instance.getId} for product ${instance.getData.productId}", exception)
        }, Duration.Inf)
    })

  def scheduler: Scheduler = {
    Scheduler.create(dataSource, task)
      .threads(schedulerConfiguration.threads)
      .registerShutdownHook()
      .build()
  }

  def dataSource: DataSource = {
    val dataSource = new PGSimpleDataSource()
    dataSource.setURL(databaseConfiguration.url)
    dataSource.setDatabaseName(databaseConfiguration.name)
    dataSource.setUser(databaseConfiguration.user)
    dataSource.setPassword(databaseConfiguration.password)

    dataSource
  }

}
