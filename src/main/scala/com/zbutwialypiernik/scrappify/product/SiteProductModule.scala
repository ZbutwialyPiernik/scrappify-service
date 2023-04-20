package com.zbutwialypiernik.scrappify.product

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.DatabaseModule
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler
import com.zbutwialypiernik.scrappify.site.SiteService

import scala.concurrent.ExecutionContext


class SiteProductModule(databaseModule: DatabaseModule, siteService: SiteService, siteProductScheduler: SiteProductScheduler)(implicit executionContext: ExecutionContext) {
  import databaseModule._

  lazy val productService = wire[SiteProductService]
  private lazy val siteProductRepository = wire[SiteProductRepository]
}
