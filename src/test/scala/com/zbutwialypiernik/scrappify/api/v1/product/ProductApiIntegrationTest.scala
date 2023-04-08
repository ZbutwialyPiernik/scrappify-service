package com.zbutwialypiernik.scrappify.api.v1.product

import akka.http.scaladsl.model.StatusCodes
import com.zbutwialypiernik.scrappify.IntegrationTest
import com.zbutwialypiernik.scrappify.api.v1.common.ValidationErrorResponse
import com.zbutwialypiernik.scrappify.api.v1.fixture.product.{ProductApiClient, ProductApiMatchers}
import com.zbutwialypiernik.scrappify.api.v1.fixture.WebsiteMocker
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import io.lemonlabs.uri.{AbsoluteUrl, Host}
import org.awaitility.Awaitility.await
import org.awaitility.scala.AwaitilitySupport

import java.util.Currency
import scala.concurrent.duration.MILLISECONDS

class ProductApiIntegrationTest extends IntegrationTest
  with WebsiteMocker
  with CommonDataGenerators
  with AwaitilitySupport
  with ProductApiMatchers
  with ProductApiClient {

  lazy val siteService = context.siteModule.siteService

  "POST /api/v1/products" when {
    "request body is valid" should {
      "create product" in {
        And("Valid create site request")
        val request = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("http://localhost/product/abcd1234"), "test", sampleCron())
        When("Call create product endpoint")
        val response = createProduct(request)
        Then("Returns valid product response")
        isValidProductResponse(response, request)
      }
    }

    "request body contains unsupported host" should {
      "throw 409 error" in {
        val request = ProductRequest("Smartphone XZY", AbsoluteUrl.parse("http://localhost/someproduct"), "test", sampleCron())

        val response = createProduct(request)

        isErrorResponse(response, StatusCodes.Conflict, s"Host ${request.url.host} is not supported")
      }
    }

    "request body is invalid" should {
      "throw 400 error" in {
        val request = ProductRequest("", AbsoluteUrl.parse("htts://localhost/someproduct"), "ABCD1234", sampleCron())

        Post("/api/v1/products", request) ~> routes ~> check {
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

  "PUT /api/v1/products/{id}" when {
    "request body is valid" should {
      "update product" in {
        val createRequest = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", sampleCron())

        val productId = isValidProductResponse(createProduct(createRequest), createRequest).id

        val updateRequest = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", sampleCron())

        isValidProductResponse(updateProduct(productId, updateRequest), updateRequest, StatusCodes.OK)
      }
    }
  }

  "GET /api/v1/products" when {
    "request body is valid" should {
      "update product" in {
        val createRequest = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", sampleCron())

        val productId = isValidProductResponse(createProduct(createRequest), createRequest).id

        val updateRequest = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", sampleCron())

        isValidProductResponse(updateProduct(productId, updateRequest), updateRequest, StatusCodes.OK)
      }
    }
  }

  "POST /api/v1/products/{id}/refresh" when {
    "product exists" should {
      "schedule new scrapping task and execute" in {
        val mockProductSnapshot = mockProductSite()

        val productRequest = ProductRequest("Note", AbsoluteUrl.parse(mockProductSnapshot.url), "ABCD1234", sampleCron())

        // Should create product from valid request
        val productResponse = createProduct(productRequest)

        // Should return product with 201 code
        val productId = isValidProductResponse(productResponse, productRequest).id

        // Request
        val refreshResponse = refreshProduct(productId)

        //
        isOkWithEmptyBody(refreshResponse)

        // Wait till async scrapper does the job
        await atMost(100, MILLISECONDS) until {
          hasValidProductSnapshot(productId, mockProductSnapshot.productName, mockProductSnapshot.price, mockProductSnapshot.currency)
        }
      }
    }
  }

  private def hasValidProductSnapshot(productId: Int, productName: Option[String], productPrice: BigDecimal, currency: Option[Currency]): Boolean = {
    val response = listProductSnapshots(productId)
    val page = isValidPage[SiteProductSnapshot](response)
    println(page)

    if (page.size != 1) {
      return false
    }

    val a = page.head.productId == productId
    val b = page.head.name == productName
    val c = page.head.price == productPrice
    val d = page.head.currency == currency

    page.size == 1
  }

}
