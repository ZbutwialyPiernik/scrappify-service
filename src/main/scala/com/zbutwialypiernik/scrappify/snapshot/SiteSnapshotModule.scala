package com.zbutwialypiernik.scrappify.snapshot

import com.softwaremill.macwire.wire

import scala.concurrent.ExecutionContext

class SiteSnapshotModule(val siteProductSnapshotRepository: SiteProductSnapshotRepository)(implicit executionContext: ExecutionContext) {
  lazy val siteProductSnapshotService = wire[SiteProductSnapshotService]
}
