package com.zbutwialypiernik.scrappify.database

import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import org.flywaydb.core.Flyway
import slick.jdbc.JdbcBackend.Database

class DatabaseModule(config: Config, configuration: DatabaseConfiguration) {
  val database = Database.forConfig("scrappify.database", config = config)

  def init(): Unit = {
    val schemaLocations = List("db/migration")
    val flyway = Flyway
      .configure()
      .locations(schemaLocations: _*)
      .dataSource(configuration.url, configuration.user, configuration.password)
      .load()
    val _ = flyway.migrate()
  }

}
