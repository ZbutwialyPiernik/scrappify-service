package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.common.ServiceError
import io.lemonlabs.uri.AbsoluteUrl

import java.time.Instant
import java.util.Currency
import scala.concurrent.Future

case class ScrappingResult(price: BigDecimal,
                           currency: Option[Currency],
                           productName: Option[String],
                           fetchTime: Instant)

trait Scrapper {

  def execute(url: AbsoluteUrl): Future[Either[ServiceError, ScrappingResult]]

  def supports(url: AbsoluteUrl): Boolean

}