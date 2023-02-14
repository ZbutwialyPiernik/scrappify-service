package com.zbutwialypiernik.scrappify.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.SchedulableInstance
import com.github.kagkarlsson.scheduler.task.helper.{PlainScheduleAndData, RecurringTaskWithPersistentSchedule}
import com.github.kagkarlsson.scheduler.task.schedule.{Schedule, Schedules}
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.product.SiteProduct

import scala.concurrent.{ExecutionContext, Future}

private class DbSiteProductScheduler(val scheduler: Scheduler, val cronTask: RecurringTaskWithPersistentSchedule[ScheduleAndString])
                                    (implicit executionContext: ExecutionContext) extends SiteProductScheduler with StrictLogging {

  override def cronSchedule(siteProduct: SiteProduct): Future[Unit] = Future {
    scheduler.schedule(schedulableInstance(siteProduct))
    logger.info(s"Scheduled new product ${siteProduct.name} (${siteProduct.id}) with cron ${siteProduct.fetchCron}")
  }

  override def instantSchedule(siteProduct: SiteProduct): Future[Unit] = Future {
    scheduler.schedule(cronTask.schedulableInstance(siteProduct.id.toString))

    logger.info(s"Scheduled new product ${siteProduct.name} (${siteProduct.id}) with cron ${siteProduct.fetchCron}")
  }

  override def updateCronSchedule(siteProduct: SiteProduct): Future[Unit] = Future {
    scheduler.reschedule(schedulableInstance(siteProduct))
   // scheduler.schedule(task.schedulableInstance())
  }

  override def cancelCronSchedule(siteProduct: SiteProduct): Future[Unit] = Future {
    scheduler.cancel(cronTask.instanceId(siteProduct.id.toString))
  }

  private def schedulableInstance(siteProduct: SiteProduct): SchedulableInstance[ScheduleAndString] = {
    val data = new ScheduleAndString(Schedules.cron(siteProduct.fetchCron.toString), siteProduct.url.toString)
    cronTask.schedulableInstance(siteProduct.id.toString, data)
  }

}

class ScheduleAndString(schedule: Schedule, data: String) extends PlainScheduleAndData(schedule, data) {
  def url: String = super.getData.asInstanceOf[String]

}