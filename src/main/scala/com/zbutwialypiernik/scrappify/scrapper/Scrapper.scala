package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.product.SiteProduct

import java.time.Instant
import java.util.Currency
import scala.concurrent.Future

case class ScrappingResult(price: BigDecimal,
                           currency: Option[Currency],
                           productName: Option[String],
                           fetchTime: Instant)

trait Scrapper {

  def execute(siteProduct: SiteProduct): Future[Either[ServiceError, ScrappingResult]]

  def supports(siteProduct: SiteProduct): Boolean

}