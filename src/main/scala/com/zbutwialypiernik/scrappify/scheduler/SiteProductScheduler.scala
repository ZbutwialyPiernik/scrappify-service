package com.zbutwialypiernik.scrappify.scheduler

import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import cron4s.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

trait SiteProductScheduler {

  def cronSchedule(productId: Int, cron: CronExpr, url: AbsoluteUrl): AsyncResult[Unit]

  def updateCronSchedule(productId: Int, cron: CronExpr, url: AbsoluteUrl): AsyncResult[Unit]
  
  def cancelCronSchedule(productId: Int): AsyncResult[Unit]
  
  def instantSchedule(productId: Int, url: AbsoluteUrl): AsyncResult[Unit]

}
