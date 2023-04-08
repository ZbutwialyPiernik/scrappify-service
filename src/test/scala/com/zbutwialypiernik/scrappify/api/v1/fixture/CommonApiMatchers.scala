package com.zbutwialypiernik.scrappify.api.v1.fixture

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.zbutwialypiernik.scrappify.api.v1.common.{ErrorResponse, JsonSupport}
import org.scalatest.matchers.should.Matchers

import scala.reflect.ClassTag

trait CommonApiMatchers
  extends JsonSupport
    with Matchers {
  self: ScalatestRouteTest =>

  def isErrorResponse(result: RouteTestResult, statusCode: StatusCode, message: String): Unit = {
    checkCodeAndExtractBody[ErrorResponse](result, statusCode) should have(
      Symbol("statusCode")(statusCode),
      Symbol("message")(message)
    )
  }

  def checkCodeAndExtractBody[T:ClassTag](result: RouteTestResult, statusCode: StatusCode)(implicit fromEntityUnmarshaller: FromEntityUnmarshaller[T]): T = {
    check {
      println(responseAs[String])
      status shouldEqual statusCode
      responseAs[T]
    }(result)
  }

  def isOkWithEmptyBody(result: RouteTestResult): Unit = {
    check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual "OK"
    }(result)
  }

}
