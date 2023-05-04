package com.zbutwialypiernik.scrappify.fixture

import com.github.javafaker.Faker
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.scrapper.ScrappingResult
import com.zbutwialypiernik.scrappify.site.Site
import cron4s.{Cron, CronExpr}
import io.lemonlabs.uri.{AbsoluteUrl, Url}

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import java.util.Currency
import java.util.concurrent.TimeUnit

trait CommonDataGenerators {

  val GBP: Currency = Currency.getInstance("GBP")
  val EUR: Currency = Currency.getInstance("EUR")
  val USD: Currency = Currency.getInstance("USD")

  val faker = new Faker()

  def sampleProduct(site: Site): SiteProduct = sampleProduct(site.host.toString, site.id)

  def sampleProduct(siteHost: String = faker.internet().domainWord(), siteId: Int = 0): SiteProduct = {
    val code = sampleProductCode()
    val url = Url(scheme = "https", host = s"$siteHost.com", path = code).toAbsoluteUrl
    SiteProduct(sampleNonNegativeInt(), sampleProductName(), url, sampleProductCode(), sampleCron(), siteId)
  }

  def sampleProductName(): String = faker.commerce().productName()

  def sampleProductCode(): String = faker.code().asin()

  def sampleScrappingResult(): ScrappingResult =
    ScrappingResult(
      samplePrice(),
      Some(Currency.getInstance(faker.currency().code())),
      Some(faker.company().name()),
      faker.date().past(1, TimeUnit.HOURS).toInstant)

  def sampleUrl(): AbsoluteUrl = AbsoluteUrl.parse("https://" + faker.internet().url())

  def sampleNonNegativeInt(max: Int = Int.MaxValue): Int = faker.random().nextInt(1, max)

  def samplePrice(): BigDecimal = BigDecimal(sampleNonNegativeInt(10000))

  def sampleCron(): CronExpr = Cron.unsafeParse("0 */30 * ? * *")

  def instantOf(second: Int = 1, minute: Int = 1, hour: Int = 1, day: Int = 1, month: Int = 1, year: Int = 2023): Instant =
    LocalDateTime.of(year, month, day, hour, minute, second).toInstant(ZoneOffset.UTC)

  def localDateOf(day: Int = 1, month: Int = 1, year: Int = 2023): LocalDate =
    LocalDate.of(year, month, day)

}
