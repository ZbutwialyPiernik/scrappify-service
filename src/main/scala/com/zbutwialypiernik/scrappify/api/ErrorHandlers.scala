package com.zbutwialypiernik.scrappify.api

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.zbutwialypiernik.scrappify.api.v1.dto.{JsonSupport, ValidationErrorResponse}
import octopus.ValidationError

import scala.util.control.NonFatal

case class ModelValidationRejection(validationErrors: List[ValidationError]) extends Rejection

trait ErrorHandlers extends SLF4JLogging with CustomDirectives with JsonSupport {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case NonFatal(e) =>
      log.error("Exception caught in handler:", e)
      completeAsError(InternalServerError, "Internal server error occurred.")
  }

  val rejectionHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ModelValidationRejection(validationErrors) =>
        complete(BadRequest, ValidationErrorResponse(BadRequest, "Request body is invalid", validationErrors.groupMap(_.path.asString)(_.message)))
    }
    .result()
    /*.mapRejectionResponse {
      case res@HttpResponse(status, _, ent: HttpEntity.Strict, _) =>
        val message = ent.data.utf8String.replaceAll("\"", """\"""")
        res.withEntity(HttpEntity(ContentTypes.`application/json`, errorResponseFormat.write(ErrorResponse(status, message)).compactPrint))

      case x => x
    }*/


}
