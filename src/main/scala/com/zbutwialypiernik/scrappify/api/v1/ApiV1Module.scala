package com.zbutwialypiernik.scrappify.api.v1

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.{Directives, ExceptionHandler, RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.zbutwialypiernik.scrappify.api.ErrorHandlers
import com.zbutwialypiernik.scrappify.api.v1.product.ProductApi
import com.zbutwialypiernik.scrappify.api.v1.site.SiteApi
import com.zbutwialypiernik.scrappify.product.ProductService
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler
import com.zbutwialypiernik.scrappify.site.SiteService

import scala.concurrent.ExecutionContext

class ApiV1Module(productService: ProductService,
                  siteService: SiteService,
                  siteProductScheduler: SiteProductScheduler)
                 (implicit system: ActorSystem, executionContext: ExecutionContext)
  extends ErrorHandlers {

  import com.softwaremill.macwire._

  lazy val routes: Route =
    cors(CorsSettings.defaultSettings.withAllowedMethods(
      Seq(GET, POST, PUT, DELETE, OPTIONS))) {
      DebuggingDirectives.logRequest("req/resp", Logging.InfoLevel) {
        DebuggingDirectives.logRequestResult("req/resp", Logging.InfoLevel) {
          handleExceptions(exceptionHandler) {
            handleRejections(rejectionHandler.withFallback(RejectionHandler.default)) {
              Directives.concat(wire[ProductApi].routes, wire[SiteApi].routes)
            }
          }
        }
      }

    }


}
