package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.common.Page
import com.zbutwialypiernik.scrappify.database.Repository.{SiteProducts, siteProductPrices, siteProducts}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.{Repository, SqlDatabase}
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.Future

class SiteProductRepository(database: SqlDatabase) extends Repository[SiteProducts, SiteProduct, Int](database) {

  override def table: TableQuery[SiteProducts] = siteProducts

  def list(siteId: Option[Int], name: Option[String], page: Page): Future[Seq[SiteProduct]] = {
    database.run(siteProducts
      .filterOpt(siteId)((a, b) => a.siteId === b)
      .drop(page.offset)
      .take(page.size)
      .result)
  }

  def listPrices(productId: Int, page: Page): Future[Seq[SiteProductSnapshot]] =
    database.run(siteProductPrices
      .filter(_.id === productId)
      .drop(page.offset)
      .take(page.size)
      .result)

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = database.run(a)

}

