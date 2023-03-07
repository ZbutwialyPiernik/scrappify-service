package com.zbutwialypiernik.scrappify.api.v1.product

import akka.http.scaladsl.model.StatusCodes
import com.zbutwialypiernik.scrappify.api.v1.common.ValidationErrorResponse
import com.zbutwialypiernik.scrappify.api.v1.fixture.product.{ProductApiExecutor, ProductApiMatchers}
import com.zbutwialypiernik.scrappify.api.v1.fixture.{IntegrationTest, WebsiteMocker}
import com.zbutwialypiernik.scrappify.fixture.{CommonParams, DataGenerators}
import com.zbutwialypiernik.scrappify.product.SiteProduct
import io.lemonlabs.uri.{AbsoluteUrl, Host}
import org.awaitility.Awaitility.await
import org.awaitility.scala.AwaitilitySupport

import scala.concurrent.duration.SECONDS

class ProductApiIntegrationTest extends IntegrationTest
  with CommonParams
  with WebsiteMocker
  with DataGenerators
  with AwaitilitySupport
  with ProductApiMatchers
  with ProductApiExecutor {

  lazy val siteService = context.siteModule.siteService

  "POST /api/v1/product" when {
    "request body is valid" should {
      "create product" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("notebook.com")))

        val request = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", validCron())

        val response = createProduct(request)

        Post("/api/v1/product", request) ~> routes ~> check {
          status shouldEqual StatusCodes.Created

          val response = responseAs[SiteProduct]
          response.id should be >= 0
          response.name shouldEqual request.name
          response.url shouldEqual request.url
          response.fetchCron shouldEqual request.fetchCron
          response.productCode shouldEqual request.productCode
        }
      }
    }

    "request body contains unsupported host" should {
      "throw 409 error" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("notebook.com")))

        val request = ProductRequest("Smartphone XZY", AbsoluteUrl.parse("https://smartphone.com/someproduct"), "test", validCron())
        val response = createProduct(request)

        isErrorResponse(response, StatusCodes.Conflict, s"Host ${request.url.host} is not supported")
      }
    }

    "request body is invalid" should {
      "throw 400 error" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("notebook.com")))

        val request = ProductRequest("", AbsoluteUrl.parse("https://notebook.com/someproduct"), "ABCD1234", validCron())

        Post("/api/v1/product", request) ~> routes ~> check {
          status shouldEqual StatusCodes.BadRequest

          val response = responseAs[ValidationErrorResponse]
          val errors = Map {
            "name" -> List("must be not empty")
          }

          response.statusCode shouldEqual StatusCodes.BadRequest
          response.message shouldEqual "Request body is invalid"
          response.errors shouldEqual errors
        }
      }
    }
  }

  "POST /api/v1/product/{id}/refresh" when {
    "product exists" should {
      "schedule new scrapping task and execute" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("localhost")))

        val siteMockUrl = mockProductSite("Notebook 1TB/16GB 16'", "50.10 zł")

        val productRequest = ProductRequest("Note", AbsoluteUrl.parse(siteMockUrl), "ABCD1234", validCron())

        // Should create product from valid request
        val productResponse = createProduct(productRequest)

        // Should return product with 201 code
        val productId = isValidProductCreatedResponse(productResponse, productRequest).id

        //
        val refreshResponse = refreshProduct(productId)

        //
        isOkWithEmptyBody(refreshResponse)

        await atMost(5, SECONDS) until {
          test(productId)
        }
      }
    }
  }

  private def test(productId: Long): Boolean = {
    val response = listProductPrices (productId)
    val page = isValidPageOfPrices (response)

    page.nonEmpty
  }

}
