package com.zbutwialypiernik.scrappify.api.v1.fixture.product

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import org.scalatest.matchers.should.Matchers

trait ProductApiExecutor extends Matchers {
  self: ScalatestRouteTest =>

  def createProduct(body: ProductRequest)(implicit toResponseMarshaller: ToEntityMarshaller[ProductRequest], routes: Route): RouteTestResult =
    makePostRequest("/api/v1/product", body)

  def refreshProduct(id: Long)(implicit routes: Route): RouteTestResult =
    Post(s"/api/v1/product/$id/refresh") ~!> routes ~> runRoute

  def listProductPrices(id: Long)(implicit routes: Route): RouteTestResult =
    Get(s"/api/v1/product/$id/prices") ~!> routes ~> runRoute

  def makePostRequest[T](url: String, body: T)(implicit toResponseMarshaller: ToEntityMarshaller[T], routes: Route): RouteTestResult =
    Post(url, body) ~!> routes ~> runRoute

}
