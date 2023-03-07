package com.zbutwialypiernik.scrappify.fixture

import com.github.javafaker.Faker
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.scrapper.ScrappingResult
import cron4s.{Cron, CronExpr}
import io.lemonlabs.uri.{AbsoluteUrl, Url}

import java.util.Currency
import java.util.concurrent.TimeUnit

trait DataGenerators {

  val faker = new Faker()

  def sampleProduct(siteHost: String = faker.company().name(), siteId: Int = 0): SiteProduct = {
    val code = randomProductCode()
    val url = Url(scheme = "https", host = s"$siteHost.com/" + code).toAbsoluteUrl
    SiteProduct(randomNonNegativeInt(), faker.commerce().productName(), url, randomProductCode(), validCron(), siteId)
  }

  def randomProductCode(): String = faker.code().asin()

  def sampleScrappingResult(): ScrappingResult =
    ScrappingResult(
      randomBigDecimal(),
      Some(Currency.getInstance(faker.currency().code())),
      Some(faker.company().name()),
      faker.date().past(1, TimeUnit.HOURS).toInstant)

  def randomUrl(): AbsoluteUrl = AbsoluteUrl.parse("https://" + faker.internet().url())

  def randomNonNegativeInt(): Int = faker.random().nextInt(1, Int.MaxValue)

  def randomBigDecimal(): BigDecimal = BigDecimal(randomNonNegativeInt())

  def validCron(): CronExpr = Cron.unsafeParse("0 */30 * ? * *")

}
