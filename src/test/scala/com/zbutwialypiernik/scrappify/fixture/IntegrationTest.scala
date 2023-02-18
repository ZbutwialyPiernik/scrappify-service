package com.zbutwialypiernik.scrappify.fixture

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.typesafe.config.ConfigFactory
import com.zbutwialypiernik.scrappify.AppContext
import com.zbutwialypiernik.scrappify.api.v1.dto.JsonSupport
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter.SetUnit

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
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

  lazy val routes: Route = context.apiV1Module.routes

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(60.seconds)

  override def afterContainersStart(container: Containers): Unit = {
    withContainers { container =>
      context = new AppContext {
        implicit lazy val system: ActorSystem = ActorSystem("scrappify")
        implicit lazy val executionContext: ExecutionContext = system.dispatcher

        override val config = ConfigFactory.parseMap(asJava(Map(
          "scrappify.database.url" -> container.jdbcUrl,
          "scrappify.database.user" -> container.username,
          "scrappify.database.name" -> container.databaseName,
          "scrappify.database.password" -> container.password,
        )))
          .withFallback(ConfigFactory.load())
          .resolve()
      }

      context.init()
    }
  }

  override def beforeContainersStop(container: Containers): Unit = {
    //context.databaseModule.sqlDatabase.close()
  }

  override def afterEach(): Unit = {
    val databaseName =  context.configurationModule.databaseConfiguration.name

    val truncatesFuture = context.databaseModule.sqlDatabase.run(
      sql"SELECT table_name FROM information_schema.tables WHERE table_schema = '#$databaseName';".as[String]
    ).map {
      names => names.map(name => SQLActionBuilder(List(s"TRUNCATE TABLE $name CASCADE"), SetUnit).asUpdate)
    }

    Await.result(truncatesFuture.flatMap {
      truncates =>
        context.databaseModule.sqlDatabase.run(DBIO.sequence(truncates))
    }, 5.seconds)
  }

}
