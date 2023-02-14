package com.zbutwialypiernik.scrappify.snapshot

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.DatabaseModule

import scala.concurrent.ExecutionContext

class SiteSnapshotModule(val databaseModule: DatabaseModule)(implicit executionContext: ExecutionContext) {
  import databaseModule._

  lazy val siteProductSnapshotService = wire[SiteProductSnapshotService]
  private lazy val siteProductSnapshotRepository = wire[SiteProductSnapshotRepository]
}
