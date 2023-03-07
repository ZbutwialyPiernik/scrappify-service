package com.zbutwialypiernik.scrappify.api.v1.common

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCode
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.site.Site
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import cron4s.Cron
import cron4s.expr.CronExpr
import io.lemonlabs.uri.{AbsoluteUrl, Host}
import spray.json._

import java.time.Instant
import java.util.Currency

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object CronExprFormat extends JsonFormat[CronExpr] {
    def write(cron: CronExpr) = JsString(cron.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(cron) => Cron.unsafeParse(cron)
        case _ => deserializationError("Expected valid Cron string")
      }
    }
  }

  implicit object AbsoluteUrlFormat extends JsonFormat[AbsoluteUrl] {
    def write(url: AbsoluteUrl) = JsString(url.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(url) => AbsoluteUrl.parse(url)
        case _ => deserializationError("Expected valid url string")
      }
    }
  }

  implicit object HostFormat extends JsonFormat[Host] {
    def write(url: Host) = JsString(url.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(url) => Host.parse(url)
        case _ => deserializationError("Expected valid url string")
      }
    }
  }

  implicit object CurrencyFormat extends JsonFormat[Currency] {
    def write(currency: Currency) = JsString(currency.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(currency) => Currency.getInstance(currency)
        case _ => deserializationError("Expected valid currency string")
      }
    }
  }

  implicit object StatusCodeFormat extends JsonFormat[StatusCode] {
    def write(statusCode: StatusCode) = JsNumber(statusCode.intValue())

    def read(value: JsValue) = {
      value match {
        case JsNumber(statusCode) => StatusCode.int2StatusCode(statusCode.intValue)
        case _ => deserializationError("Expected valid currency string")
      }
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    def write(instant: Instant) = JsString(instant.toString)

    def read(value: JsValue) = {
      value match {
        case JsString(date) => Instant.parse(date)
        case _ => deserializationError("Expected valid UTC date")
      }
    }
  }

  implicit val productRequestFormat = jsonFormat4(ProductRequest.apply)
  implicit val productResponseFormat = jsonFormat6(SiteProduct)
  implicit val productPriceResponseFormat = jsonFormat6(SiteProductSnapshot.apply)
  implicit val siteResponseFormat = jsonFormat3(Site.apply)
  implicit val errorResponseFormat = jsonFormat2(ErrorResponse.apply)
  implicit val validationErrorResponseFormat = jsonFormat3(ValidationErrorResponse.apply)

}
