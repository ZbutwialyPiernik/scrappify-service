package com.zbutwialypiernik.scrappify.api.v1.fixture

import com.github.mustachejava.DefaultMustacheFactory
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.BeforeAndAfterAll

import java.io.StringWriter
import java.util.UUID

trait WebsiteMocker extends StrictLogging {
  self: BeforeAndAfterAll =>

  private val mustacheFactory = new DefaultMustacheFactory()
  private val wireMockServer = new WireMockServer()
  private lazy val productTemplate = mustacheFactory.compile(mustacheFactory.getReader("mock/product.html"), "product")

  def mockProductSite(productName: String, price: String): String = {
    val writer = new StringWriter()
    productTemplate.execute(writer, Map(
      "productName" -> productName,
      "price" -> price,
    ))
    val body = writer.toString
    val urlSuffix = s"/product/${UUID.randomUUID()}"
    wireMockServer.stubFor(
      get(urlSuffix)
        .willReturn(aResponse()
          .withBody(body)
          .withStatus(200))
    )
    val requestUrl = wireMockServer.url(urlSuffix)

    logger.debug(s"Mocked product page with product name: $productName, price: $price at $requestUrl")

    requestUrl
  }

  override protected def beforeAll(): Unit = wireMockServer.start()

  override protected def afterAll(): Unit = wireMockServer.stop()

}
