package com.zbutwialypiernik.scrappify.api.v1.fixture

import com.github.mustachejava.DefaultMustacheFactory
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.IntegrationTest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import slick.jdbc.PostgresProfile.api._


import java.io.StringWriter
import scala.concurrent.duration.DurationInt
import java.util.{Currency, UUID}
import scala.concurrent.Await

case class MockProductSnapshot(productName: Option[String],
                               price: BigDecimal,
                               url: String = "",
                               currency: Option[Currency] = Some(Currency.getInstance("USD")))




trait WebsiteMocker extends StrictLogging
  with CommonDataGenerators {
  self: IntegrationTest =>

  private val mustacheFactory = new DefaultMustacheFactory()
  private val wireMockServer = new WireMockServer()
  private val productTemplate = mustacheFactory.compile(mustacheFactory.getReader("mock/product.html"), "product")

  def mockProductSite(): MockProductSnapshot = {
    val writer = new StringWriter()
    val mockData = MockProductSnapshot(Some(faker.company().name()), samplePrice())

    productTemplate.execute(writer, mockData)
    val body = writer.toString
    val urlSuffix = s"/products/${UUID.randomUUID()}"
    wireMockServer.stubFor(
      get(urlSuffix)
        .willReturn(aResponse()
          .withBody(body)
          .withStatus(200))
    )
    val requestUrl = wireMockServer.url(urlSuffix)

    logger.debug(s"Mocked product page with product name: ${mockData.productName}, price: ${mockData.price} at $requestUrl\n$body")

    mockData.copy(url = requestUrl)
  }

  override def afterDatabaseStart(container: Containers): Unit = {
    wireMockServer.start()

    Await.result (context.databaseModule.database.run(
      sql"INSERT INTO site (name, host) VALUES ('Mock shop', 'localhost') ON CONFLICT host DO NOTHING;".asUpdate
    ), 5.seconds)
  }

  override protected def afterAll(): Unit = wireMockServer.stop()

}
