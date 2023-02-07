package com.zbutwialypiernik.scrappify.scrapper

import cats.data.EitherT
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.product.SiteProduct

import scala.concurrent.{ExecutionContext, Future}

case class UnsupportedProductException(productId: Long, url: String) extends ServiceError(s"Scrapper for product $productId and $url not found")

class ScrappingService(scrappers: Set[Scrapper])(implicit executionContext: ExecutionContext) {

  def performScrapping(product: SiteProduct): AsyncResult[ScrappingResult] =
    EitherT.fromOptionF(Future.successful(scrappers.find(_.supports(product))), UnsupportedProductException(product.id, product.url.toString))
      .flatMapF(_.execute(product))

}
