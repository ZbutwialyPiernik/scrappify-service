package com.zbutwialypiernik.scrappify.api.v1.fixture.product

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.zbutwialypiernik.scrappify.api.v1.fixture.CommonApiMatchers
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import com.zbutwialypiernik.scrappify.product.SiteProduct
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot

trait ProductApiMatchers extends CommonApiMatchers {
  self: ScalatestRouteTest =>

  def isValidProductCreatedResponse(result: RouteTestResult, request: ProductRequest): SiteProduct = {
    val response = checkCodeAndExtractBody[SiteProduct](result, StatusCodes.Created)
    response.id should be >= 0
    response.name shouldEqual request.name
    response.url shouldEqual request.url
    response.fetchCron shouldEqual request.fetchCron
    response.productCode shouldEqual request.productCode
    response
  }

  def isValidPageOfPrices(result: RouteTestResult): Seq[SiteProductSnapshot] = {
    val response = checkCodeAndExtractBody[Seq[SiteProductSnapshot]](result, StatusCodes.OK)
    response
  }

}
