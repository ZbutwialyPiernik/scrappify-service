package com.zbutwialypiernik.scrappify.product

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.SqlDatabase
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler
import com.zbutwialypiernik.scrappify.site.SiteService

import scala.concurrent.ExecutionContext


class SiteProductModule(val siteProductRepository: SiteProductRepository, val siteService: SiteService, siteProductScheduler: SiteProductScheduler)(implicit executionContext: ExecutionContext) {
  lazy val productService = wire[ProductService]
}
