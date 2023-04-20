package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.common.{Page, PageRequest}
import com.zbutwialypiernik.scrappify.database.Repository
import com.zbutwialypiernik.scrappify.database.Repository.{SiteProducts, siteProductPrices, siteProducts, sites}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.product.SiteProductRepository.getProductWithPrice
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import cron4s.Cron
import io.lemonlabs.uri.AbsoluteUrl
import slick.dbio.DBIO
import slick.jdbc.GetResult

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Currency
import scala.concurrent.{ExecutionContext, Future}


class SiteProductRepository(database: Database)(implicit executionContext: ExecutionContext) extends Repository[SiteProducts, SiteProduct, Int](database) {

  override def table: TableQuery[SiteProducts] = siteProducts

  def list(siteId: Option[Int], name: Option[String], pageRequest: PageRequest): Future[Page[SiteProductWithPrice]] = database.run {
    val query =
      siteProducts
        .joinLeft(siteProductPrices)
        .on(_.id === _.productId)
        .distinctOn(_._1.id)
        .filterOpt(siteId)((a, b) => a._1.siteId === b)
        .sortBy(_._2.map(_.fetchTime).desc)
        .sortBy(_._1.id)
        .map(pair => combineWithPrice(pair._1, pair._2))

    paginate(
      pageRequest,
      query
    )
  }

  def findProductWithPrice(productId: Int): DBIO[Option[SiteProductWithPrice]] =
    siteProducts
      .joinLeft(siteProductPrices)
      .on(_.id === _.productId)
      .filter(_._1.id === productId)
      .sortBy(_._2.map(_.fetchTime).desc)
      .take(1)
      .map(pair => combineWithPrice(pair._1, pair._2))
      .result
      .headOption



  private def combineWithPrice(p: SiteProducts, ps: Rep[Option[Repository.SiteProductSnapshots]]) = {
    ((p.id, p.name, p.url, p.code, p.fetchCron, p.siteId, ps.map(_.price), ps.flatMap(_.currency), ps.map(_.fetchTime))
      <> (SiteProductWithPrice.tupled, SiteProductWithPrice.unapply))
  }

}

object SiteProductRepository {
  implicit def getProductWithPrice: GetResult[SiteProductWithPrice] = GetResult(r =>
    SiteProductWithPrice(
      r.<<,
      r.<<,
      AbsoluteUrl.parse(r.nextString()),
      r.<<,
      Cron.unsafeParse(r.nextString()),
      r.<<,
      r.<<,
      r.nextStringOption().map(Currency.getInstance),
      r.nextTimestampOption().map(_.toInstant),
    )
  )

}

