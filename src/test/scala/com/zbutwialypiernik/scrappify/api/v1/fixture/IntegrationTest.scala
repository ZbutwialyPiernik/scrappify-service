package com.zbutwialypiernik.scrappify.api.v1.fixture

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.typesafe.config.{Config, ConfigFactory}
import com.zbutwialypiernik.scrappify.AppContext
import com.zbutwialypiernik.scrappify.api.v1.common.JsonSupport
import com.zbutwialypiernik.scrappify.scrapper.{Scrapper, ScrappingModule}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter.SetUnit

import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import scala.jdk.javaapi.CollectionConverters.asJava

trait IntegrationTest
  extends AnyWordSpec
    with TestContainerForAll
    with BeforeAndAfterEach
    with ScalatestRouteTest
    with JsonSupport
    with Matchers {
  override val containerDef = PostgreSQLContainer.Def(dockerImageName = "postgres:15.1-alpine", databaseName = "scrappify", username = "scrappify", password = "scrappify")

  var context: AppContext = null

  implicit lazy val routes: Route = context.apiV1Module.routes

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(60.seconds)

  override def afterContainersStart(containers: Containers) = {
    buildContext(containers)

    context.init()
  }

  def buildContext(container: Containers): Unit = {
    context = new AppContext {
      implicit lazy val system = ActorSystem("scrappify")
      implicit lazy val executionContext = system.dispatcher
      override lazy val scrappingModule = new ScrappingModule(JsoupBrowser(), clock, siteProductSnapshotModule.siteProductSnapshotService)(implicitly(executionContext)) {
        override lazy val scrappers = wireSet[Scrapper] + new WiremockScrapper(clock, browser)(implicitly(executionContext))
      }
      override lazy val config = loadConfig(container)
    }
  }

  def loadConfig(container: Containers): Config =
    ConfigFactory.parseMap(asJava(Map(
      "scrappify.database.url" -> container.jdbcUrl,
      "scrappify.database.user" -> container.username,
      "scrappify.database.name" -> container.databaseName,
      "scrappify.database.password" -> container.password,
    )))
      .withFallback(ConfigFactory.load())
      .resolve()

  override def beforeContainersStop(container: Containers): Unit =
    context.databaseModule.database.close()

  override def afterEach(): Unit = {
    val databaseName = context.configurationModule.databaseConfiguration.name

    val truncatesFuture = context.databaseModule.database.run(
      sql"SELECT table_name FROM information_schema.tables WHERE table_schema = '#$databaseName';".as[String]
    ).map {
      names => names.map(name => SQLActionBuilder(List(s"TRUNCATE TABLE $name CASCADE"), SetUnit).asUpdate)
    }

    Await.result(truncatesFuture.flatMap {
      truncates =>
        context.databaseModule.database.run(DBIO.sequence(truncates))
    }, 5.seconds)
  }

}
