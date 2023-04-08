package com.zbutwialypiernik.scrappify.api.v1.fixture.product

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import com.zbutwialypiernik.scrappify.api.v1.fixture.CommonApiMatchers
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import com.zbutwialypiernik.scrappify.product.SiteProduct

trait ProductApiMatchers extends CommonApiMatchers {
  self: ScalatestRouteTest =>

  def isValidProductResponse(result: RouteTestResult, request: ProductRequest, statusCode: StatusCode = StatusCodes.Created): SiteProduct = {
    val response = checkCodeAndExtractBody[SiteProduct](result, statusCode)
    response.id should be >= 0
    response.name shouldEqual request.name
    response.url shouldEqual request.url
    response.fetchCron shouldEqual request.fetchCron
    response.code shouldEqual request.code
    response
  }

  def isValidPage[T](result: RouteTestResult)(implicit fromEntityUnmarshaller: FromEntityUnmarshaller[Seq[T]]): Seq[T] = {
    val response = checkCodeAndExtractBody[Seq[T]](result, StatusCodes.OK)
    response
  }

}
