package com.zbutwialypiernik.scrappify.scheduler

import com.zbutwialypiernik.scrappify.product.SiteProduct

import scala.concurrent.Future

trait SiteProductScheduler {

  def cronSchedule(SiteProduct: SiteProduct): Future[Unit]

  def updateCronSchedule(SiteProduct: SiteProduct): Future[Unit]
  
  def cancelCronSchedule(SiteProduct: SiteProduct): Future[Unit]
  
  def instantSchedule(SiteProduct: SiteProduct): Future[Unit]

}
