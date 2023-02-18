package com.zbutwialypiernik.scrappify.database

import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import org.flywaydb.core.Flyway
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

final class SqlDatabase(db: slick.jdbc.JdbcBackend.Database) {

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = db.run(a)

  def close(): Unit = {
    db.close()
  }
}

object SqlDatabase {

  def create(config: Config): SqlDatabase = {
    val db = Database.forConfig("scrappify.database", config = config)
    new SqlDatabase(db)
  }

}