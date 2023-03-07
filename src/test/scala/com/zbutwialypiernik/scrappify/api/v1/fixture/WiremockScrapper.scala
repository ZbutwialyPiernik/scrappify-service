package com.zbutwialypiernik.scrappify.api.v1.fixture

import com.zbutwialypiernik.scrappify.scrapper.SimpleHtmlScrapper
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document

import java.time.Clock
import java.util.Currency
import scala.concurrent.ExecutionContext

class WiremockScrapper(clock: Clock, browser: Browser)(implicit executionContext: ExecutionContext) extends SimpleHtmlScrapper(clock, browser) {
  override def findPrice(document: Document): Option[BigDecimal] = document >?> text("div.price")
    .map(BigDecimal.apply)

  override def findCurrency(document: Document): Option[Currency] = Some(Currency.getInstance("USD"))

  override def findProductName(document: Document): Option[String] = document >?> text("h1")

  override def supportedHosts: Set[String] = Set("localhost")
}
