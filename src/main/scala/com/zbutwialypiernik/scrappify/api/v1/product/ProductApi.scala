package com.zbutwialypiernik.scrappify.api.v1.product

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.data._
import com.zbutwialypiernik.scrappify.api.Api
import com.zbutwialypiernik.scrappify.common.NotFoundError
import com.zbutwialypiernik.scrappify.product.{ProductService, UnsupportedSiteError}
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler

import scala.concurrent.ExecutionContext

class ProductApi(val productService: ProductService,
                         val siteProductScheduler: SiteProductScheduler)
                        (implicit executionContext: ExecutionContext) extends Api {

  val routes: Route = apiPrefix(1, "product") {
    concat(
      pathEnd {
        get {
          withPageParams {
            page => {
              parameters(
                Symbol("name").as[String].optional
              ) { name =>
                complete(productService.listProducts(name = name, page = page))
              }
            }
          }
        }
        post {
          validatedEntity(as[ProductRequest]).apply { request =>
            onSuccess(productService.create(request).value) {
              case Left(error: UnsupportedSiteError) => completeAsError(StatusCodes.Conflict, error.message)
              case Right(product) => complete(StatusCodes.Created, product)
            }
          }
        }
      },
      path(IntNumber) { id =>
        get {
          val product = productService.findProductById(id)

          rejectEmptyResponse {
            complete(product)
          }
        }
      },
      path(IntNumber / "prices") { id =>
        withPageParams { page =>
          get {
            complete(productService.listPrices(id, page))
          }
        }
      },
      path(IntNumber / "refresh") { id =>
        post {
          onSuccess(productService.requestProductPriceRefresh(id).value) {
            case Left(error: NotFoundError) => completeAsError(StatusCodes.NotFound, error.message)
            case Right(_) => complete(StatusCodes.OK)
          }
        }
      }
    )
  }

}
