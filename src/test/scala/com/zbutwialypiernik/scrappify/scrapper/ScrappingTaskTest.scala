package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.BaseUnitTest
import com.zbutwialypiernik.scrappify.common.{AsyncResult, InternalServiceError}
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.{SiteProductSnapshot, SiteProductSnapshotService}
import io.lemonlabs.uri.AbsoluteUrl
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class ScrappingTaskTest extends BaseUnitTest {

  val productSnapshotService = mock[SiteProductSnapshotService]
  val scrappingService = mock[ScrappingService]
  val task = new ScrappingTask(productSnapshotService, scrappingService)

  "execute" when {
    "url is supported and scrapping service returns valid result" should {
      "register new snapshot" in {
        val productId = sampleNonNegativeInt()
        val productUrl = sampleUrl()

        val scrappingResult = sampleScrappingResult()
        val snapshot = SiteProductSnapshot(sampleNonNegativeInt(), scrappingResult.price, scrappingResult.currency, scrappingResult.name, scrappingResult.fetchTime, productId)

        (scrappingService.performScrapping _).expects(productUrl).returning(AsyncResult.success(scrappingResult))
        (productSnapshotService.registerSnapshot _).expects(productId, scrappingResult).returning(Future.successful(snapshot))

        task.execute(productId, productUrl).value map { result =>
          result.isRight shouldEqual true
          result match {
            case Right(v) => v shouldEqual snapshot
          }
        }
      }
    }

    "product exists and scrapping service returns error" should {
      "pass down error" in {
        val productId = sampleNonNegativeInt()
        val productUrl = sampleUrl()

        (scrappingService.performScrapping _).expects(productUrl).returning(AsyncResult.failure(InternalServiceError("some error")))

        task.execute(productId, productUrl).value map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(v) => v shouldEqual InternalServiceError("some error")
          }
        }
      }
    }
  }

}
