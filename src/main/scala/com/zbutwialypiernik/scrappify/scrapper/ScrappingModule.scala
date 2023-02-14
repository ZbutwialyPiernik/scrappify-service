package com.zbutwialypiernik.scrappify.scrapper

import com.softwaremill.macwire._
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshotService
import net.ruippeixotog.scalascraper.browser.Browser

import java.time.Clock
import scala.concurrent.ExecutionContext

class ScrappingModule(val browser: Browser, val clock: Clock, val siteProductSnapshotService: SiteProductSnapshotService)(implicit executionContext: ExecutionContext) {
  lazy val xKomScrapper = wire[XKomScrapper]
  lazy val scrappers = wireSet[Scrapper]

  lazy val scrappingService = wire[ScrappingService]
  lazy val scrappingTask = wire[ScrappingTask]
}
