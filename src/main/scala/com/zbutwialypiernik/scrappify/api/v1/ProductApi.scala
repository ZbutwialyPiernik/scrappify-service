package com.zbutwialypiernik.scrappify.api.v1

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.data._
import com.zbutwialypiernik.scrappify.api.Api
import com.zbutwialypiernik.scrappify.api.v1.dto.ProductRequest
import com.zbutwialypiernik.scrappify.product.{ProductService, UnsupportedSiteError}
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler

import scala.concurrent.ExecutionContext

private class ProductApi(val productService: ProductService, val siteProductScheduler: SiteProductScheduler)
                        (implicit system: ActorSystem, executionContext: ExecutionContext) extends Api {

  val routes: Route = apiPrefix(1, "product") {
    concat(
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
      },
      post {
        validatedEntity(as[ProductRequest]).apply { request =>
          onSuccess(productService.create(request)) {
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
            complete(productService.listPrices(id, page))
          }
        }
      },
      path(IntNumber / "refresh") { id =>
        post {
          onSuccess(OptionT(productService.findProductById(id))
            .semiflatMap(product => siteProductScheduler.instantSchedule(product))
            .value) {
            case Some(_) => complete(StatusCodes.Created)
            case None => completeAsError(StatusCodes.NotFound, "Product ")
          }
        }
      }
    )
  }

}
