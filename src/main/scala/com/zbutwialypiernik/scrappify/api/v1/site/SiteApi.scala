package com.zbutwialypiernik.scrappify.api.v1.site

import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, concat, get, parameters, path, rejectEmptyResponse}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import com.zbutwialypiernik.scrappify.api.Api
import com.zbutwialypiernik.scrappify.product.SiteProductService
import com.zbutwialypiernik.scrappify.site.SiteService

class SiteApi(productService: SiteProductService,
              siteService: SiteService)
  extends Api {

  val routes: Route = apiPrefix(1, "site") {
    concat(
      withPageParams {
        page => {
          parameters(
            Symbol("name").as[String].optional,
          ) { name =>
            get {
              val sites = siteService.listSites(name, page)

              complete(sites)
            }
          }
        }
      },
      path(IntNumber) { id =>
        get {
          val product = siteService.findById(id)

          rejectEmptyResponse {
            complete(product)
          }
        }
      },
      path(IntNumber / "products") { id =>
        withPageParams { page =>
          get {
            val products = productService.listProducts(Some(id), page = page)

            complete(products)
          }
        }
      }
    )
  }

}
