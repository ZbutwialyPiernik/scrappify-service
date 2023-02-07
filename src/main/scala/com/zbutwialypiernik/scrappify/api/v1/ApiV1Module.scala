package com.zbutwialypiernik.scrappify.api.v1

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.zbutwialypiernik.scrappify.api.ErrorHandlers
import com.zbutwialypiernik.scrappify.product.ProductService
import com.zbutwialypiernik.scrappify.site.SiteService

import scala.concurrent.ExecutionContext

class ApiV1Module(productService: ProductService, siteService: SiteService)
                 (implicit system: ActorSystem, executionContext: ExecutionContext)
  extends ErrorHandlers {

  import com.softwaremill.macwire._

  lazy val routes: Route =
    cors(CorsSettings.defaultSettings.withAllowedMethods(
      Seq(GET, POST, PUT, DELETE, OPTIONS))) {
      handleExceptions(exceptionHandler) {
        handleRejections(rejectionHandler) {
          Directives.concat(wire[ProductApi].routes, wire[SiteApi].routes)
        }
      }
    }


}
