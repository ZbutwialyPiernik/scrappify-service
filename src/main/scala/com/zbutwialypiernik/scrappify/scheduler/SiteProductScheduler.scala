package com.zbutwialypiernik.scrappify.scheduler

import com.zbutwialypiernik.scrappify.product.SiteProduct

import scala.concurrent.Future

trait SiteProductScheduler {

  def cronScheduleProduct(SiteProduct: SiteProduct): Future[Unit]

  def instantScheduleProduct(SiteProduct: SiteProduct): Future[Unit]

}
