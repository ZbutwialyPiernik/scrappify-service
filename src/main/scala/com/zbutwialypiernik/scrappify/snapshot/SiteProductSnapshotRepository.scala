package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.database.Repository.{SiteProductSnapshots, siteProductPrices}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.{Repository, SqlDatabase}
class SiteProductSnapshotRepository(database: SqlDatabase) extends Repository[SiteProductSnapshots, Int](database){
  override def table = siteProductPrices

  override protected def getId(row: SiteProductSnapshots) = row.id

}