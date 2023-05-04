package com.zbutwialypiernik.scrappify.snapshot

import com.softwaremill.macwire.wire
import com.zbutwialypiernik.scrappify.database.DatabaseModule

import java.time.Clock
import scala.concurrent.ExecutionContext

class SiteSnapshotModule(databaseModule: DatabaseModule, clock: Clock)(implicit executionContext: ExecutionContext) {
  import databaseModule._

  lazy val siteProductSnapshotService = wire[SiteProductSnapshotService]
  private lazy val siteProductSnapshotRepository = wire[SiteProductSnapshotRepository]
}
