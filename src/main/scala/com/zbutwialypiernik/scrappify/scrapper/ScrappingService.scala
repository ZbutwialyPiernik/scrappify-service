package com.zbutwialypiernik.scrappify.scrapper

import cats.data.EitherT
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.common.ServiceError
import io.lemonlabs.uri.AbsoluteUrl

import scala.concurrent.{ExecutionContext, Future}

case class UnsupportedSiteError(url: String) extends ServiceError(s"Scrapper for site $url not found")

class ScrappingService(scrappers: Set[Scrapper])(implicit executionContext: ExecutionContext) {

  def performScrapping(url: AbsoluteUrl): AsyncResult[ScrappingResult] =
    EitherT.fromOptionF(Future.successful(scrappers.find(_.supports(url: AbsoluteUrl))), UnsupportedSiteError(url.toString))
      .flatMap(_.execute(url))

}
