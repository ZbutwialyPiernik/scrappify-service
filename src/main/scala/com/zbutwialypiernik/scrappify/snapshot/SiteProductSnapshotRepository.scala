package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.common.{Page, PageRequest}
import com.zbutwialypiernik.scrappify.database.Repository.{SiteProductSnapshots, siteProductPrices}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.Repository
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class SiteProductSnapshotRepository(database: Database)(implicit executionContext: ExecutionContext) extends Repository[SiteProductSnapshots, SiteProductSnapshot, Int](database) {
  override def table = siteProductPrices

  def list(productId: Int, pageRequest: PageRequest): Future[Page[SiteProductSnapshot]] =
    database.run(
      paginate(pageRequest,
        siteProductPrices
          .filter(_.productId === productId)
      )
    )
}
