package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.DatabaseIntegrationTest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.product.{SiteProduct, SiteProductRepository}
import com.zbutwialypiernik.scrappify.snapshot.fixture.SnapshotGenerator

import java.time.LocalDate
import scala.Seq
import scala.concurrent.ExecutionContext

class SiteProductSnapshotRepositoryTest
  extends DatabaseIntegrationTest
    with CommonDataGenerators
    with SnapshotGenerator {

  implicit val executionContext = ExecutionContext.global

  def siteProductSnapshotRepository = new SiteProductSnapshotRepository(databaseModule.database)

  def siteProductRepository = new SiteProductRepository(databaseModule.database)


  "Should find price from product with latest fetch time during a day within range" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other valid product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices for first product")
    createSnapshots(
      sampleSnapshot(product.id, 10, GBP),
      sampleSnapshot(product.id, 80, GBP, day = 5),
      sampleSnapshot(product.id, 70, GBP, day = 5, hour = 22),
      sampleSnapshot(product.id, 10, GBP, day = 8),
      sampleSnapshot(product.id, 20, GBP, day = 27, hour = 5),
      sampleSnapshot(product.id, 60, GBP, day = 27),
      sampleSnapshot(product.id, 60, GBP, day = 31),
      sampleSnapshot(product.id, 70, GBP, month = 2, hour = 10),
      sampleSnapshot(product.id, 30, USD, month = 3, day = 3, hour = 8)
    )
    And("Snapshots for other product")
    createSnapshots(
      sampleSnapshot(product2.id, 100, GBP, hour = 23),
      sampleSnapshot(product2.id, 110, EUR, day = 2, hour = 23),
      sampleSnapshot(product2.id, 120, USD, day = 3, hour = 23)
    )
    When("Fetch daily price from january")
    val januaryPrices = retrieveDailyLatestPriceInRange(product.id, Some(localDateOf(1)), localDateOf(31))
    Then("Should return biggest daily prices order asc")
    januaryPrices should contain theSameElementsInOrderAs Seq(
      DailyPrice(localDateOf(day = 1), BigDecimal(10.00)),
      DailyPrice(localDateOf(day = 5), BigDecimal(70)),
      DailyPrice(localDateOf(day = 8), BigDecimal(10)),
      DailyPrice(localDateOf(day = 27), BigDecimal(20)),
      DailyPrice(localDateOf(day = 31), BigDecimal(60))
    )
  }

  "Should find price from product with latest fetch time during a day earlier than end date" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other valid product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices for first product")
    createSnapshots(
      sampleSnapshot(product.id, 10, GBP),
      sampleSnapshot(product.id, 80, GBP, day = 5),
      sampleSnapshot(product.id, 70, GBP, day = 5, hour = 22),
      sampleSnapshot(product.id, 10, GBP, day = 8),
      sampleSnapshot(product.id, 20, GBP, day = 27, hour = 5),
      sampleSnapshot(product.id, 60, GBP, day = 27),
      sampleSnapshot(product.id, 60, GBP, day = 31),
      sampleSnapshot(product.id, 70, GBP, month = 2, hour = 10),
      sampleSnapshot(product.id, 30, USD, month = 3, day = 3, hour = 8)
    )
    And("Snapshots for other product")
    createSnapshots(
      sampleSnapshot(product2.id, 100, GBP, hour = 23),
      sampleSnapshot(product2.id, 110, EUR, day = 2, hour = 23),
      sampleSnapshot(product2.id, 120, USD, day = 3, hour = 23)
    )
    When("Fetch daily price before march")
    val januaryPrices = retrieveDailyLatestPriceInRange(product.id, None, localDateOf(month = 3, day = 31))
    Then("Should return biggest daily prices order asc")
    januaryPrices should contain theSameElementsInOrderAs Seq(
      DailyPrice(localDateOf(day = 1), BigDecimal(10)),
      DailyPrice(localDateOf(day = 5), BigDecimal(70)),
      DailyPrice(localDateOf(day = 8), BigDecimal(10)),
      DailyPrice(localDateOf(day = 27), BigDecimal(20)),
      DailyPrice(localDateOf(day = 31), BigDecimal(60)),
      DailyPrice(localDateOf(month = 2), BigDecimal(70)),
      DailyPrice(localDateOf(month = 3, day = 3), BigDecimal(30)),
    )
  }

  "Should return empty list when product has no defined snapshots" in {
    Given("Valid product")
    val product = createValidProduct()
    And("Other valid product")
    val product2 = createValidProduct()
    And("Snapshots with different fetch times and different prices")
    createSnapshots(
      sampleSnapshot(product2.id, 100, GBP, hour = 23),
      sampleSnapshot(product2.id, 110, EUR, day = 2, hour = 23),
      sampleSnapshot(product2.id, 120, USD, day = 3, hour = 23)
    )
    When("Fetch daily price from january")
    val januaryPrices = retrieveDailyLatestPriceInRange(product.id, Some(localDateOf(1)), localDateOf(31))
    Then("Should empty list")
    januaryPrices shouldBe empty
  }

  private def retrieveDailyLatestPriceInRange(productId: Int, startDateOption: Option[LocalDate], end: LocalDate): Seq[DailyPrice] =
    runBlocking(siteProductSnapshotRepository.retrieveDailyLatestPriceInRange(productId, startDateOption, end))

  private def createSnapshots(siteProductSnapshot: SiteProductSnapshot*): Unit =
    runBlocking(siteProductSnapshot.map(siteProductSnapshotRepository.create(_)))

  private def createValidProduct(): SiteProduct =
    runBlocking {
      siteProductRepository.createAndFetch(sampleProduct())
    }

}