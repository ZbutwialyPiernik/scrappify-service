package com.zbutwialypiernik.scrappify.scrapper

import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.ServiceError
import com.zbutwialypiernik.scrappify.product.SiteProduct
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model.Document

import java.time.Clock
import java.util.Currency
import scala.concurrent.{ExecutionContext, Future}

case class PriceNotExtractedError(product: SiteProduct, siteBody: String)
  extends ServiceError(s"Could not extract price for product ${product.name}#${product.id} from ${product.url.toString}")

abstract class SimpleHtmlScrapper(clock: Clock, browser: Browser)(implicit executionContext: ExecutionContext) extends Scrapper with StrictLogging {

  def findPrice(document: Document): Option[BigDecimal]

  def findCurrency(document: Document): Option[Currency]

  def findProductName(document: Document): Option[String]

  def supportedHosts: Set[String]

  override def execute(product: SiteProduct): Future[Either[ServiceError, ScrappingResult]] = {
    val productUrl = product.url.toString()

    Future(browser.get(productUrl))
      .map(document => {
        findPrice(document)
          .map(price => {
            val result = ScrappingResult(price, findCurrency(document), findProductName(document), clock.instant())

            logger.debug(s"Fetched results from: $productUrl", result)

            result
          })
          .toRight({
            val error = PriceNotExtractedError(product, document.toHtml)

            logger.info(error.message)
            logger.debug(s"Could not find price in $productUrl \n${error.siteBody}")

            error
          })
      })
  }

  override def supports(siteProduct: SiteProduct): Boolean = siteProduct.url.host.value ==

}
