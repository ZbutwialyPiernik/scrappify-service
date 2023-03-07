package com.zbutwialypiernik.scrappify.scheduler

import cats.data.EitherT
import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.SchedulableInstance
import com.github.kagkarlsson.scheduler.task.helper.{OneTimeTask, PlainScheduleAndData, RecurringTaskWithPersistentSchedule}
import com.github.kagkarlsson.scheduler.task.schedule.{Schedule, Schedules}
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import cron4s.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

import scala.concurrent.ExecutionContext

private class DbSiteProductScheduler(val scheduler: Scheduler,
                                     val cronTask: RecurringTaskWithPersistentSchedule[ScheduleAndString],
                                     val oneTimeTask: OneTimeTask[String])
                                    (implicit executionContext: ExecutionContext) extends SiteProductScheduler with StrictLogging {

  override def cronSchedule(productId: Int, cron: CronExpr, url: AbsoluteUrl): AsyncResult[Unit] = EitherT.rightT {
    scheduler.schedule(schedulableCronInstance(productId, cron, url))
    logger.info(s"Scheduled new cron scrapper for product ($productId) with cron $cron and $url")
  }

  override def updateCronSchedule(productId: Int, cron: CronExpr, url: AbsoluteUrl): AsyncResult[Unit] = EitherT.rightT {
    scheduler.reschedule(schedulableCronInstance(productId, cron, url))

    logger.info(s"Updated cron schedule scrapper for product $productId with cron $cron and $url")
  }

  override def cancelCronSchedule(productId: Int): AsyncResult[Unit] = EitherT.rightT  {
    scheduler.cancel(cronTask.instanceId(productId.toString))

    logger.info(s"Canceling cron scrapper for product $productId")
  }

  override def instantSchedule(productId: Int, url: AbsoluteUrl): AsyncResult[Unit] = EitherT.rightT  {
    scheduler.schedule(oneTimeTask.schedulableInstance(productId.toString, url.toString))

    logger.info(s"Scheduled new instant scrapper execution for product ${productId} at url $url")
  }

  private def schedulableCronInstance(productId: Int, cron: CronExpr, url: AbsoluteUrl): SchedulableInstance[ScheduleAndString] = {
    val data = new ScheduleAndString(Schedules.cron(cron.toString), url.toString)
    cronTask.schedulableInstance(productId.toString, data)
  }

}

class ScheduleAndString(schedule: Schedule, data: String) extends PlainScheduleAndData(schedule, data) {
  def url: AbsoluteUrl = AbsoluteUrl.parse(super.getData.asInstanceOf[String])

}