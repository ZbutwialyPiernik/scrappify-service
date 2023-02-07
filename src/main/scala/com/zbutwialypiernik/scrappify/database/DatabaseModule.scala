package com.zbutwialypiernik.scrappify.database

import com.softwaremill.macwire._
import com.typesafe.config.Config
import com.zbutwialypiernik.scrappify.config.DatabaseConfiguration
import com.zbutwialypiernik.scrappify.product.SiteProductRepository
import com.zbutwialypiernik.scrappify.site.SiteRepository
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshotRepository


class DatabaseModule(val config: Config, val configuration: DatabaseConfiguration) {
  lazy val sqlDatabase = wireWith(SqlDatabase.create _)
  lazy val siteRepository = wire[SiteRepository]
  lazy val siteProductRepository= wire[SiteProductRepository]
  lazy val siteProductSnapshotRepository = wire[SiteProductSnapshotRepository]

}
