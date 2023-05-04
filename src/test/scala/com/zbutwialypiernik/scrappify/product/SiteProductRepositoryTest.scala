package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.DatabaseIntegrationTest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.fixture.SnapshotGenerator
import com.zbutwialypiernik.scrappify.snapshot.{SiteProductSnapshot, SiteProductSnapshotRepository}
import org.scalatest.Assertion
import slick.dbio.DBIO

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.Currency
import scala.concurrent.ExecutionContext

class SiteProductRepositoryTest
  extends DatabaseIntegrationTest
    with CommonDataGenerators
    with SnapshotGenerator {

  implicit val executionContext = ExecutionContext.global

  def siteProductRepository = new SiteProductRepository(databaseModule.database)

  def siteProductSnapshotRepository = new SiteProductSnapshotRepository(databaseModule.database)

  "Should find price with latest fetch time" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other valid product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices")
    createSnapshots(
      sampleSnapshot(product.id, 80, GBP, 1),
      sampleSnapshot(product.id, 60, EUR, 2),
      sampleSnapshot(product.id, 60, USD, 3, 5),
      sampleSnapshot(product.id, 50, USD, 3, 8)
    )
    And("Snapshots for other product")
    createSnapshots(
      sampleSnapshot(product2.id, 100, GBP, 1, 23),
      sampleSnapshot(product2.id, 100, EUR, 2, 23),
      sampleSnapshot(product2.id, 100, USD, 3, 23)
    )
    When("Fetch first product with price by id")
    val productWithPriceOption = findProductWithPrice(product.id)
    Then("Product should have price of snapshot with latest fetch time")
    shouldMatchProductAndPriceCombined(productWithPriceOption, product, 50, USD, instantOf(day = 3, hour = 8))
  }

  "Should return empty options find when item has no defined prices" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other valid product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices")
    createSnapshots(
      sampleSnapshot(product2.id, 80, GBP, 1),
      sampleSnapshot(product2.id, 60, EUR, 2),
      sampleSnapshot(product2.id, 60, USD, 3, 5),
      sampleSnapshot(product2.id, 50, USD, 3, 8)
    )
    When("Fetch product with price by id")
    val productWithPriceOption = findProductWithPrice(product.id)
    Then("Product should have price of snapshot with latest fetch time")
    shouldMatchProductAndPrice(productWithPriceOption, product, None, None, None)
  }

  private def findProductWithPrice(productId: Int) = {
    runBlocking(siteProductRepository.findProductWithPrice(productId))
  }

  private def shouldMatchProductAndPriceCombined(productWithPriceOption: Option[SiteProductWithPrice], product: SiteProduct, price: Int, currency: Currency, lastUpdate: Instant): Option[Assertion] =
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

  private def createSnapshots(siteProductSnapshot: SiteProductSnapshot*): Unit = {
    runBlocking(siteProductSnapshot.map(siteProductSnapshotRepository.create(_)))
  }

  private def createValidProduct(): SiteProduct =
    runBlocking {
      siteProductRepository.createAndFetch(sampleProduct())
    }

}

