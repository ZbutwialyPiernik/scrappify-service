package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.support.CommonParams
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should._
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class ScrappingServiceTest extends AsyncWordSpec
  with Matchers
  with AsyncMockFactory
  with CommonParams {

  val scrapper = mock[Scrapper]
  val scrappingService = new ScrappingService(Set(scrapper))

  "performScrapping" when {
    "product site has supported scrapper" should {
      "execute scrapper and return results" in {
        (scrapper.supports _).expects(validProduct).returning(true)
        (scrapper.execute _).expects(validProduct).returning(Future.successful(Right(validScrappingResult)))

        scrappingService.performScrapping(validProduct).value map { result =>
          result.isRight shouldEqual true
          result match {
            case Right(v) => v shouldEqual validScrappingResult
          }
        }
      }
    }

    "product site does not have supported scrapper" should {
      "not execute scrapper and return failure" in {
        (scrapper.supports _).expects(validProduct).returning(false)

        scrappingService.performScrapping(validProduct).value map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(exception) =>
              exception shouldBe a [UnsupportedProductException]
              exception should have(
                Symbol("productId")(validProduct.id),
                Symbol("url")(validProduct.url.toString)
              )
          }
        }
      }
    }
  }
}
