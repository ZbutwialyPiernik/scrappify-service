package com.zbutwialypiernik.scrappify.database

import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import org.flywaydb.core.Flyway
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

final class SqlDatabase(db: slick.jdbc.JdbcBackend.Database, driver: JdbcProfile, databaseConfig: DatabaseConfiguration) {
  def updateSchema(): Unit = {
    val schemaLocations = driver match {
      case slick.jdbc.PostgresProfile => List("db/migration")
    }
    val flyway = Flyway
      .configure()
      .locations(schemaLocations: _*)
      .dataSource(databaseConfig.url, databaseConfig.user, databaseConfig.password)
      .load()
    val _ = flyway.migrate()
  }

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = db.run(a)

  def close(): Unit = {
    db.close()
  }
}

object SqlDatabase {

  def create(config: Config, configuration: DatabaseConfiguration): SqlDatabase = {
    val db = Database.forConfig("scrappify.database", config = config)
    new SqlDatabase(db, slick.jdbc.PostgresProfile, configuration)
  }

}