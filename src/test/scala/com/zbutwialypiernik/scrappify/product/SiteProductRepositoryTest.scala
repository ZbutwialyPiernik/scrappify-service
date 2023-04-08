package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.DatabaseIntegrationTest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.{SiteProductSnapshot, SiteProductSnapshotRepository}
import slick.dbio.DBIO

import java.time.{LocalDate, ZoneId}
import scala.concurrent.ExecutionContext

class SiteProductRepositoryTest extends DatabaseIntegrationTest
  with CommonDataGenerators {

  implicit val executionContext = ExecutionContext.global

  def siteProductRepository = new SiteProductRepository(databaseModule.database)

  def siteProductSnapshotRepository = new SiteProductSnapshotRepository(databaseModule.database)

  "Should find price with latest id, when few got the same date-time" in {
    Given("Valid product")
    val product = sampleProduct()


    databaseModule.database.run {
      siteProductRepository.createAndFetch(product)
    }.map(_.id)
      .flatMap(productId => databaseModule.database.run {
        DBIO.seq(
          createSimpleSnapshot(productId, 100, 1),
          createSimpleSnapshot(productId, 80, 1),
          createSimpleSnapshot(productId, 60, 1),
          createSimpleSnapshot(productId, 70, 1),
          createSimpleSnapshot(productId, 90, 1)
        )
      })
      .map(xd => {
        println("hehe")
        xd shouldEqual Option.empty
      })

    databaseModule.database.run {
      DBIO.seq(
        createSimpleSnapshot(0, 100, 1),
        createSimpleSnapshot(0, 80, 1),
        createSimpleSnapshot(0, 60, 1),
        createSimpleSnapshot(0, 70, 1),
        createSimpleSnapshot(0, 90, 1)
      )
    }
    println("SSSSS")
  }

  private def createSimpleSnapshot(productId: Int, price: Int, dayOfMonth: Int): DBIO[Int] = {
    siteProductSnapshotRepository.create(SiteProductSnapshot(0, price, Option.empty, Option.empty, LocalDate.of(2023, 1, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant, productId))
  }

}

