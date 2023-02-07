package e2e

import akka.http.scaladsl.model.StatusCodes
import com.zbutwialypiernik.scrappify.api.v1.dto.{ErrorResponse, ProductRequest, SiteRequest, ValidationErrorResponse}
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.support.{CommonParams, IntegrationTest}
import io.lemonlabs.uri.{AbsoluteUrl, Host}

class ProductApiIntegrationTest extends IntegrationTest with CommonParams {

  lazy val siteService = context.siteModule.siteService

  "POST /api/v1/product" when {
    "request body is valid" should {
      "create product" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("notebook.com")))

        val request = ProductRequest("Notebook XYZ", AbsoluteUrl.parse("https://notebook.com/product/abcd1234"), "test", validCronOne)

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

        val request = ProductRequest("Smartphone XZY", AbsoluteUrl.parse("https://smartphone.com/someproduct"), "test", validCronOne)

        Post("/api/v1/product", request) ~> routes ~> check {
          status shouldEqual StatusCodes.Conflict

          responseAs[ErrorResponse] should have(
            Symbol("statusCode") (StatusCodes.Conflict),
            Symbol("message") (s"Host ${request.url.host} is not supported")
          )
        }
      }
    }

    "request body is invalid" should {
      "throw 400 error" in {
        siteService.createSite(SiteRequest(name = "Notebook site", Host.parse("notebook.com")))

        val request = ProductRequest("", AbsoluteUrl.parse("https://notebook.com/someproduct"), "ABCD1234", validCronOne)

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

}
