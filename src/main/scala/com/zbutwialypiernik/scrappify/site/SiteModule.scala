package com.zbutwialypiernik.scrappify.site

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.SqlDatabase
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler

import scala.concurrent.ExecutionContext

class SiteModule(val siteRepository: SiteRepository)(implicit executionContext: ExecutionContext) {
  lazy val siteService = wire[SiteService]
}
