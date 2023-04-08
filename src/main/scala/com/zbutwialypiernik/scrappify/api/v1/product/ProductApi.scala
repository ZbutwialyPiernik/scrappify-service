package com.zbutwialypiernik.scrappify.api.v1.product

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.zbutwialypiernik.scrappify.api.Api
import com.zbutwialypiernik.scrappify.common.NotFoundError
import com.zbutwialypiernik.scrappify.product.{ProductService, UnsupportedSiteError}
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler

import scala.concurrent.ExecutionContext

class ProductApi(val productService: ProductService,
                 val siteProductScheduler: SiteProductScheduler)
                (implicit executionContext: ExecutionContext) extends Api {

  val routes: Route = apiPrefix(1, "products") {
    concat(
      pathPrefix(IntNumber) { id =>
        concat(
          pathEnd {
            get {
              val product = productService.findProductById(id)

              rejectEmptyResponse {
                complete(product)
              }
            }
          },
          path("snapshots") {
            withPageParams { page =>
              get {
                complete(productService.listPrices(id, page))
              }
            }
          },
          path("refresh") {
            post {
              onSuccess(productService.requestProductPriceRefresh(id).value) {
                case Left(error: NotFoundError) => completeAsError(StatusCodes.NotFound, error.message)
                case Right(_) => complete(StatusCodes.OK)
              }
            }
          },
        )
      },
      pathEnd {
        concat(
          get {
            parameters(Symbol("name").as[String].optional) {
              name => {
                withPageParams {
                  page => {
                    {
                      complete(productService.listProducts(name = name, page = page))
                    }
                  }
                }
              }
            }
          },
          post {
            validatedEntity(as[ProductRequest]).apply { request =>
              onSuccess(productService.create(request).value) {
                case Left(error: UnsupportedSiteError) => completeAsError(StatusCodes.Conflict, error.message)
                case Right(product) => complete(StatusCodes.Created, product)
              }
            }
          }
        )
      }
    )
  }

}
