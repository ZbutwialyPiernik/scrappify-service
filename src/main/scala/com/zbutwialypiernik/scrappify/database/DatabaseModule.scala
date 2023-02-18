package com.zbutwialypiernik.scrappify.database

import com.softwaremill.macwire._
import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import org.flywaydb.core.Flyway

class DatabaseModule(config: Config, configuration: DatabaseConfiguration) {
  lazy val sqlDatabase = wireWith(SqlDatabase.create _)

  def updateSchema(): Unit = {
    val schemaLocations = List("db/migration")
    val flyway = Flyway
      .configure()
      .locations(schemaLocations: _*)
      .dataSource(configuration.url, configuration.user, configuration.password)
      .load()
    val _ = flyway.migrate()
  }

  def init(): Unit = {
    updateSchema()
  }

}
