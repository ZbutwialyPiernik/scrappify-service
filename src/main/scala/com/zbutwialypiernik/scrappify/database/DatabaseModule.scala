package com.zbutwialypiernik.scrappify.database

import com.softwaremill.macwire._
import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration

class DatabaseModule(config: Config, configuration: DatabaseConfiguration) {
  lazy val sqlDatabase = wireWith(SqlDatabase.create _)
}
