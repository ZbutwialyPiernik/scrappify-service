package com.zbutwialypiernik.scrappify.api.v1.dto

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

trait BaseErrorResponse {
  def statusCode: StatusCode

  def message: String
}

case class ErrorResponse(statusCode: StatusCode, message: String) extends BaseErrorResponse

case class ValidationErrorResponse(statusCode: StatusCode = StatusCodes.BadRequest,
                                   message: String = "Request body is invalid",
                                   errors: Map[String, List[String]]) extends BaseErrorResponse
