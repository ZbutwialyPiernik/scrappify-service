package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.product.SiteProduct
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import java.time.Clock
import java.util.Currency
import scala.concurrent.ExecutionContext


class XKomScrapper(clock: Clock, browser: Browser)(implicit executionContext: ExecutionContext) extends SimpleHtmlScrapper(clock, browser) {

  override def findPrice(document: Document): Option[BigDecimal] =
    document >?> text(".sc-n4n86h-4")
      .map(_.replace(" zł", "")
        .replaceAll("\\s", "")
        .replace(",", "."))
      .map(BigDecimal.apply)

  override def findCurrency(document: Document): Option[Currency] = Some(Currency.getInstance("PLN"))

  override def findProductName(document: Document): Option[String] = document >?> text("h1")

  override def supports(siteProduct: SiteProduct): Boolean = siteProduct.url.host.value == "x-kom.pl"

  override def supportedHosts: Set[String] = Set("x-kom.pl")

}
