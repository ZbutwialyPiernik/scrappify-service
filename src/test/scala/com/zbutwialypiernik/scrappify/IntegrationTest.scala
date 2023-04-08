package com.zbutwialypiernik.scrappify

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.zbutwialypiernik.scrappify.api.v1.common.JsonSupport
import com.zbutwialypiernik.scrappify.api.v1.fixture.WiremockScrapper
import com.zbutwialypiernik.scrappify.database.DatabaseModule
import com.zbutwialypiernik.scrappify.scrapper.ScrappingModule
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import scala.concurrent.duration.DurationInt

trait IntegrationTest extends DatabaseIntegrationTest
    with ScalatestRouteTest
    with JsonSupport {

  override val containerDef = PostgreSQLContainer.Def(dockerImageName = "postgres:15.1-alpine", databaseName = "scrappify", username = "scrappify", password = "scrappify")

  var context: AppContext = null

  implicit lazy val routes: Route = context.apiV1Module.routes

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(60.seconds)

  override def afterDatabaseStart(container: Containers): Unit = {
    buildContext(container)
    context.init()
  }

  def buildContext(container: Containers): Unit = {
    context = new AppContext {
      implicit lazy val system = ActorSystem("scrappify")
      implicit lazy val executionContext = system.dispatcher
      override lazy val databaseModule: DatabaseModule = databaseModule
      override lazy val scrappingModule = new ScrappingModule(JsoupBrowser(), clock, siteProductSnapshotModule.siteProductSnapshotService)(implicitly(executionContext)) {
        override lazy val scrappers = Set(xKomScrapper, new WiremockScrapper(clock, browser)(implicitly(executionContext)))
      }
      override lazy val config = loadConfig(container)
    }
  }

  /*
  override def beforeEach(): Unit = {
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
  }*/

}
