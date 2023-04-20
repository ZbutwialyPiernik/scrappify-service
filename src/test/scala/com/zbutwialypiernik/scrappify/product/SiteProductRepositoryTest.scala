package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.DatabaseIntegrationTest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.{SiteProductSnapshot, SiteProductSnapshotRepository}
import org.scalatest.Assertion
import slick.dbio.DBIO

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.Currency
import scala.concurrent.ExecutionContext

class SiteProductRepositoryTest extends DatabaseIntegrationTest
  with CommonDataGenerators {

  implicit val executionContext = ExecutionContext.global

  def siteProductRepository = new SiteProductRepository(databaseModule.database)

  def siteProductSnapshotRepository = new SiteProductSnapshotRepository(databaseModule.database)

  private val GBP: Currency = Currency.getInstance("GBP")
  private val EUR: Currency = Currency.getInstance("EUR")
  private val USD: Currency = Currency.getInstance("USD")

  "Should find price with latest fetch time" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Snapshots with different fetch times and different prices")
    runBlocking(
      createSimpleSnapshot(product.id, 80, GBP, 1),
      createSimpleSnapshot(product.id, 60, EUR, 2),
      createSimpleSnapshot(product.id, 60, USD, 3, 5),
      createSimpleSnapshot(product.id, 50, USD, 3, 8)
    )
    When("Fetch product with price by id")
    val productWithPriceOption = findProductWithPrice(product.id)
    Then("Product should have price of snapshot with latest fetch time")
    shouldReturnProductAndPriceCombined(productWithPriceOption, product, 50, USD, instantOf(day = 3, hour = 8))
  }

  "Should return empty options find when item has no defined prices" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices")
    runBlocking(
      createSimpleSnapshot(product2.id, 80, GBP, 1),
      createSimpleSnapshot(product2.id, 60, EUR, 2),
      createSimpleSnapshot(product2.id, 60, USD, 3, 5),
      createSimpleSnapshot(product2.id, 50, USD, 3, 8)
    )
    When("Fetch product with price by id")
    val productWithPriceOption = findProductWithPrice(product.id)
    Then("Product should have price of snapshot with latest fetch time")
    shouldMatchProductAndPrice(productWithPriceOption, product, None, None, None)
  }

  private def findProductWithPrice(productId: Int) = {
    runBlocking(siteProductRepository.findProductWithPrice(productId))
  }

  private def shouldReturnProductAndPriceCombined(productWithPriceOption: Option[SiteProductWithPrice], product: SiteProduct, price: Int, currency: Currency, lastUpdate: Instant): Option[Assertion] =
    shouldMatchProductAndPrice(productWithPriceOption, product, Some(price), Some(currency), Some(lastUpdate))

  private def shouldMatchProductAndPrice(productWithPriceOption: Option[SiteProductWithPrice], product: SiteProduct, priceOption: Option[Int], currencyOption: Option[Currency], lastUpdateOption: Option[Instant]): Option[Assertion] = {
    productWithPriceOption.isDefined shouldEqual true
    productWithPriceOption.map { productWithPrice =>
      productWithPrice.id shouldEqual product.id
      productWithPrice.name shouldEqual product.name
      productWithPrice.url shouldEqual product.url
      productWithPrice.fetchCron shouldEqual product.fetchCron
      productWithPrice.siteId shouldEqual product.siteId
      productWithPrice.latestPrice shouldEqual priceOption
      productWithPrice.currency shouldEqual currencyOption
      productWithPrice.lastUpdate shouldEqual lastUpdateOption
    }
  }

  private def createValidProduct(): SiteProduct =
    runBlocking {
      siteProductRepository.createAndFetch(sampleProduct())
    }

  private def createSimpleSnapshot(productId: Int, price: Int, currency: Currency, dayOfMonth: Int, hour: Int = 1): DBIO[Int] =
    siteProductSnapshotRepository.create(SiteProductSnapshot(0, price, Some(currency), Option.empty, instantOf(day = dayOfMonth, hour = hour), productId))

  private def instantOf(year: Int = 2023, month: Int = 1, day: Int = 1, hour: Int = 1, minute: Int = 1, second: Int = 1): Instant =
    LocalDateTime.of(year, month, day, hour, minute, second).toInstant(ZoneOffset.UTC)


}

