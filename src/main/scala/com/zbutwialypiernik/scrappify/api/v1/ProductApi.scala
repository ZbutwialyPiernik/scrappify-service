package com.zbutwialypiernik.scrappify.api.v1

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.zbutwialypiernik.scrappify.api.Api
import com.zbutwialypiernik.scrappify.api.v1.dto.ProductRequest
import com.zbutwialypiernik.scrappify.common.Page
import com.zbutwialypiernik.scrappify.product.{ProductService, UnsupportedSiteError}

private class ProductApi(productService: ProductService) extends Api {

  val routes: Route = apiPrefix(1, "product") {
    concat(
      withPageParams {
        page => {
          parameters(
            Symbol("name").as[String].optional
          ) { name =>
            get {
              complete(productService.listProducts(name = name, page = page))
            }
          }
        }
      },
      post {
        validatedEntity(as[ProductRequest]).apply { request =>
          onSuccess(productService.createProduct(request)) {
            case Left(error: UnsupportedSiteError) => completeAsError(StatusCodes.Conflict, error.message)
            case Right(product) => complete(StatusCodes.Created, product)
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
            complete(productService.listPrices(id,  page))
          }
        }
      }
    )
  }

}
