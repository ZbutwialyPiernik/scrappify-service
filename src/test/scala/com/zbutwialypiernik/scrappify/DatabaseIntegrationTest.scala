package com.zbutwialypiernik.scrappify

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.typesafe.config.{Config, ConfigFactory}
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import com.zbutwialypiernik.scrappify.database.DatabaseModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.wordspec.AnyWordSpec
import slick.dbio.Effect.All
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.jdk.javaapi.CollectionConverters.asJava

trait DatabaseIntegrationTest
  extends AnyWordSpec
  with GivenWhenThen
  with TestContainerForAll
  with BeforeAndAfterEach
  with Matchers {

  override val containerDef = PostgreSQLContainer.Def(dockerImageName = "postgres:15.1-alpine", databaseName = "scrappify", username = "scrappify", password = "scrappify")

  var databaseModule: DatabaseModule = null

  override def afterContainersStart(container: Containers): Unit = {
    val config = loadConfig(container)
    databaseModule = new DatabaseModule(config, DatabaseConfiguration(container.jdbcUrl,container.username, container.databaseName, container.password));
    afterDatabaseStart(container)
  }

  def afterDatabaseStart(container: Containers): Unit = databaseModule.init()

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
    databaseModule.database.close()

  def runBlocking[R](a: DBIO[R]): R = Await.result(databaseModule.database.run(a), 1 seconds)

  def runBlocking(a: DBIO[Any]*): Unit = Await.result(databaseModule.database.run(DBIO.sequence(a)), 1 seconds)


}

