package com.zbutwialypiernik.scrappify.site

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.DatabaseModule

import scala.concurrent.ExecutionContext

class SiteModule(val databaseModule: DatabaseModule)(implicit executionContext: ExecutionContext) {
  import databaseModule._

  lazy val siteService = wire[SiteService]
  private lazy val siteRepository = wire[SiteRepository]
}
