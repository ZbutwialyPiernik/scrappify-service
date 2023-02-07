package com.zbutwialypiernik.scrappify.scheduler

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.TaskInstanceId
import com.github.kagkarlsson.scheduler.task.helper.{PlainScheduleAndData, RecurringTaskWithPersistentSchedule}
import com.github.kagkarlsson.scheduler.task.schedule.{Schedule, Schedules}
import com.zbutwialypiernik.scrappify.product.SiteProduct

import java.lang
import scala.concurrent.{ExecutionContext, Future}

class DbSiteProductScheduler(val scheduler: Scheduler, val task: RecurringTaskWithPersistentSchedule[ScheduleAndInt])(implicit executionContext: ExecutionContext) extends SiteProductScheduler {

  override def cronScheduleProduct(siteProduct: SiteProduct): Future[Unit] = Future {
    val data = new ScheduleAndInt(Schedules.cron(siteProduct.fetchCron.toString), siteProduct.id)
    scheduler.schedule(task.schedulableInstance(siteProduct.id.toString, data))
  }

  override def instantScheduleProduct(siteProduct: SiteProduct): Future[Unit] = Future {
    scheduler.cancel(TaskInstanceId.of("site-product-scrapper", siteProduct.id.toString))
  }

}

class ScheduleAndInt(schedule: Schedule, data: Int) extends PlainScheduleAndData(schedule, data) {
  def productId = super.getData.asInstanceOf[lang.Integer]

}