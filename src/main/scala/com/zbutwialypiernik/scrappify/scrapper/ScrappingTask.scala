package com.zbutwialypiernik.scrappify.scrapper

import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.snapshot.{SiteProductSnapshot, SiteProductSnapshotService}
import io.lemonlabs.uri.AbsoluteUrl

import scala.concurrent.ExecutionContext

class ScrappingTask(val productSnapshotService: SiteProductSnapshotService, val scrappingService: ScrappingService)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def execute(id: Integer, url: AbsoluteUrl): AsyncResult[SiteProductSnapshot] = {
    scrappingService.performScrapping(url)
      .semiflatMap(result => productSnapshotService.registerSnapshot(id, result))
  }

}
