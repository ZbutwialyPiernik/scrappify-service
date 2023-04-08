package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.BaseUnitTest
import com.zbutwialypiernik.scrappify.common.AsyncResult

class ScrappingServiceTest extends BaseUnitTest {

  val scrapper = mock[Scrapper]
  val scrapper2 = mock[Scrapper]
  val scrappingService = new ScrappingService(Set(scrapper, scrapper2))

  "performScrapping" when {
    "url has supported scrapper" should {
      "execute scrapper and return results" in {
        Given("Url to scrap")
          val url = sampleUrl()
        And("Non-matching scrapper")
          (scrapper.supports _).expects(url).returning(false)
        And("Matching scrapper that will return valid scrapping result")
          val scrappingResult = sampleScrappingResult()
          (scrapper2.supports _).expects(url).returning(true)
          (scrapper2.execute _).expects(url).returning(AsyncResult.success(scrappingResult))
        When("Call scrapper")
          val result = scrappingService.performScrapping(url).value
        Then("Should return Right with scrapping result")
          result map { result =>
            result.isRight shouldEqual true
            result match {
              case Right(v) => v shouldEqual scrappingResult
            }
          }
      }
    }

    "url does not have supported scrapper" should {
      "not execute scrapper and return error" in {
        Given("Url to scrap")
        val url = sampleUrl()
        And("Non matching scrappers")
        (scrapper.supports _).expects(url).returning(false)
        (scrapper2.supports _).expects(url).returning(false)
        When("Call scrapper")
        val result = scrappingService.performScrapping(url).value
        Then("Returns Left with UnsupportedSiteError")
        result map { result =>
          result.isLeft shouldEqual true
          result match {
            case Left(exception) =>
              exception shouldBe a [UnsupportedSiteError]
              exception should have(
                Symbol("url")(url.toString())
              )
          }
        }
      }
    }
  }
}
