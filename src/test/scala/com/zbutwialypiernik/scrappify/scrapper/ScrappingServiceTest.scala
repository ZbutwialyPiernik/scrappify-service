package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.common.AsyncResult
import com.zbutwialypiernik.scrappify.fixture.{CommonParams, FakeDataGenerators}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should._
import org.scalatest.wordspec.AsyncWordSpec

class ScrappingServiceTest extends AsyncWordSpec
  with Matchers
  with FakeDataGenerators
  with AsyncMockFactory
  with CommonParams {

  val scrapper = mock[Scrapper]
  val scrappingService = new ScrappingService(Set(scrapper))

  "performScrapping" when {
    "url has supported scrapper" should {
      "execute scrapper and return results" in {
        val url = randomUrl()
        val scrappingResult = fakeScrappingResult()

        (scrapper.supports _).expects(url).returning(true)
        (scrapper.execute _).expects(url).returning(AsyncResult.success(scrappingResult))

        scrappingService.performScrapping(url).value map { result =>
          result.isRight shouldEqual true
          result match {
            case Right(v) => v shouldEqual scrappingResult
          }
        }
      }
    }

    "url does not have supported scrapper" should {
      "not execute scrapper and return failure" in {
        val url = randomUrl()

        (scrapper.supports _).expects(url).returning(false)

        scrappingService.performScrapping(url).value map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(exception) =>
              exception shouldBe a [UnsupportedSiteException]
              exception should have(
                Symbol("url")(url.toString())
              )
          }
        }
      }
    }
  }
}
