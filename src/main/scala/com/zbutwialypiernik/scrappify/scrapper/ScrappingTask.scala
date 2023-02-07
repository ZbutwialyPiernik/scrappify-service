package com.zbutwialypiernik.scrappify.scrapper

import cats.data.EitherT
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.product.{SiteProduct, SiteProductSnapshot}
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshotService

import scala.concurrent.{ExecutionContext, Future}

case class ProductNotFoundError(val siteId: Long) extends ServiceError(s"Product with id $siteId not found")

class ScrappingTask(val productProvider: Int => Future[Option[SiteProduct]], val productSnapshotService: SiteProductSnapshotService, val scrappingService: ScrappingService)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def execute(id: Int): AsyncResult[SiteProductSnapshot] = {
    EitherT.fromOptionF(productProvider(id), ProductNotFoundError(id))
      .flatMap(scrappingService.performScrapping)
      .semiflatMap(result => productSnapshotService.registerSnapshot(id, result.price, result.currency, result.fetchTime))
  }

}
