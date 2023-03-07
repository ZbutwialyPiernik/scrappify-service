package com.zbutwialypiernik.scrappify.api

import akka.http.scaladsl.common.NameDefaultReceptacle
import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import com.zbutwialypiernik.scrappify.api.v1.common.{ErrorResponse, JsonSupport}
import com.zbutwialypiernik.scrappify.common.Page
import octopus.Validator
import octopus.syntax._

trait CustomDirectives {
  this: JsonSupport =>

  private val pageParameter: NameDefaultReceptacle[Int] = Symbol("page").as[Int].withDefault(0)
  private val sizeParameter: NameDefaultReceptacle[Int] = Symbol("size").as[Int].withDefault(20)

  def apiPrefix[L](version: Int, pm: PathMatcher[L]): Directive[L] = {
    pathPrefix("api" / s"v${version}" / pm)
  }

  def withPageParams: Directive1[Page] = {
    parameters(pageParameter, sizeParameter).as(Page.apply _)
  }

  def validatedEntity[T](um: FromRequestUnmarshaller[T])(implicit validator: Validator[T]): Directive1[T] =
    entity(um).flatMap {
      model =>
        model.validate.toEither match {
          case Left(errors) => reject(ModelValidationRejection(errors))
          case Right(_) => provide(model)
        }
    }

  def completeAsError(status: StatusCode, message: String)(implicit m: ToEntityMarshaller[ErrorResponse]): StandardRoute =
    complete(status, ErrorResponse(status, message))


}
