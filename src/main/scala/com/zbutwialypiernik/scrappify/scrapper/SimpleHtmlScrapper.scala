package com.zbutwialypiernik.scrappify.scrapper

import cats.data.EitherT
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.common.ServiceError
import io.lemonlabs.uri.AbsoluteUrl
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model.Document

import java.time.Clock
import java.util.Currency
import scala.concurrent.{ExecutionContext, Future}

case class PriceNotExtractedError(url: AbsoluteUrl, siteBody: String)
  extends ServiceError(s"Could not extract price from $url")

abstract class SimpleHtmlScrapper(clock: Clock, browser: Browser)(implicit executionContext: ExecutionContext) extends Scrapper with StrictLogging {

  def findPrice(document: Document): Option[BigDecimal]

  def findCurrency(document: Document): Option[Currency]

  def findProductName(document: Document): Option[String]

  def supportedHosts: Set[String]

  override def execute(url: AbsoluteUrl): AsyncResult[ScrappingResult] = {
    val productUrl = url.toString()

    EitherT(Future(browser.get(productUrl))
      .map(document => {
        findPrice(document)
          .map(price => {
            val result = ScrappingResult(price, findCurrency(document), findProductName(document), clock.instant())
            logger.debug(s"Fetched results from: $productUrl", result)
            result
          })
          .toRight({
            val error = PriceNotExtractedError(url, document.toHtml)
            logger.error(s"${error.message}\n${error.siteBody}")
            error
          })
      }))

  }

  override def supports(url: AbsoluteUrl): Boolean = supportedHosts.contains(url.host.value)

}
