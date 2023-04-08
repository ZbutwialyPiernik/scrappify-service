package com.zbutwialypiernik.scrappify.api.v1.fixture.product

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import org.scalatest.matchers.should.Matchers

trait ProductApiClient extends Matchers {
  self: ScalatestRouteTest =>

  def createProduct(body: ProductRequest)(implicit toResponseMarshaller: ToEntityMarshaller[ProductRequest], routes: Route): RouteTestResult =
    makePostRequest("/api/v1/products", body)

  def updateProduct(id: Int, body: ProductRequest)(implicit toResponseMarshaller: ToEntityMarshaller[ProductRequest], routes: Route): RouteTestResult =
    makePutRequest(s"/api/v1/products/$id", body)

  def refreshProduct(id: Int)(implicit routes: Route): RouteTestResult =
    Post(s"/api/v1/products/$id/refresh") ~!> routes ~> runRoute

  def listProductSnapshots(id: Int)(implicit routes: Route): RouteTestResult =
    Get(s"/api/v1/products/$id/snapshot") ~!> routes ~> runRoute

  def makePostRequest[T](url: String, body: T)(implicit toResponseMarshaller: ToEntityMarshaller[T], routes: Route): RouteTestResult =
    Post(url, body) ~!> routes ~> runRoute

  def makePutRequest[T](url: String, body: T)(implicit toResponseMarshaller: ToEntityMarshaller[T], routes: Route): RouteTestResult =
    Post(url, body) ~!> routes ~> runRoute

}
