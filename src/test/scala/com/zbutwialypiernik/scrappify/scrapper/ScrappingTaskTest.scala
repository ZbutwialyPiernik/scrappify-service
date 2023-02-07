package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.common.{AsyncResult, InternalServiceError}
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshotService
import com.zbutwialypiernik.scrappify.support.CommonParams
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class ScrappingTaskTest extends AsyncWordSpec
  with Matchers
  with AsyncMockFactory
  with CommonParams {

  val productSnapshotService = mock[SiteProductSnapshotService]
  val scrappingService = mock[ScrappingService]

  "execute" when {
    "product exists and scrapping service returns valid result" should {
      "register new snapshot" in {
        val task = createTask(someProductProvider(validProduct))

        (scrappingService.performScrapping _).expects(validProduct).returning(AsyncResult.success(validScrappingResult))
        (productSnapshotService.registerSnapshot _).expects(validProduct.id, validScrappingResult.price, validScrappingResult.currency, validScrappingResult.fetchTime).returning(Future.successful(validProductSnapshot))

        task.execute(validProduct.id).value map { result =>
          result.isRight shouldEqual true
          result match {
            case Right(v) => v shouldEqual validProductSnapshot
          }
        }
      }
    }

    "product does not exists" should {
      "return product not found error" in {
        val productId = 25
        val task = createTask(emptyProductProvider)

        task.execute(productId).value map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(v) => v shouldEqual ProductNotFoundError(productId)
          }
        }
      }
    }

    "product exists and scrapping service returns error" should {
      "pass down error" in {
        val task = createTask(someProductProvider(validProduct))


        (scrappingService.performScrapping _).expects(validProduct).returning(AsyncResult.failure(InternalServiceError("some error")))

        task.execute(validProduct.id).value map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(v) => v shouldEqual InternalServiceError("some error")
          }
        }
      }
    }
  }

  def createTask(productProvider: Int => Future[Option[SiteProduct]]): ScrappingTask = new ScrappingTask(productProvider, productSnapshotService, scrappingService)

  def emptyProductProvider: Int => Future[Option[SiteProduct]] = _ => Future.successful(Option.empty)

  def someProductProvider(siteProduct: SiteProduct): Int => Future[Option[SiteProduct]] = _ => Future.successful(Some(siteProduct))

}
