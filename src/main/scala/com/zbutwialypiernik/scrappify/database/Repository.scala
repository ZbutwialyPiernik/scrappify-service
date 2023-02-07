package com.zbutwialypiernik.scrappify.database

import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.product.{SiteProduct, SiteProductSnapshot}
import com.zbutwialypiernik.scrappify.site.Site
import cron4s.Cron
import cron4s.expr.CronExpr
import io.lemonlabs.uri.{AbsoluteUrl, Host}
import slick.ast.BaseTypedType
import slick.dbio.{DBIO, Effect}
import slick.jdbc.JdbcType
import slick.lifted.AbstractTable
import slick.sql.FixedSqlAction

import java.time.Instant
import java.util.Currency

abstract class Repository[T <: AbstractTable[_], I: BaseTypedType](val database: SqlDatabase) extends CustomType {

  def table: TableQuery[T]

  protected def getId(row: T): Rep[I]

  private def filterById(id: I): Query[T, T#TableElementType, Seq] = table.filter(getId(_) === id)

  def findById(id: I): DBIO[Option[T#TableElementType]] = filterById(id).result.headOption

  def getById(id: I): DBIO[T#TableElementType] = filterById(id).result.head

  def create(model: T#TableElementType): DBIO[Int] = (table += model)

  //def createAndFetch(model: T#TableElementType): DBIO[T] =
    //database.run((table returning getById(_)) += model)

}

trait CustomType {

  implicit val cronExprColumnType: JdbcType[CronExpr] with BaseColumnType[CronExpr] = MappedColumnType
    .base[CronExpr, String](
      d => d.toString,
      d => Cron.unsafeParse(d)
    )

  implicit val currencyColumnType: JdbcType[Currency] with BaseColumnType[Currency] = MappedColumnType
    .base[Currency, String](
      d => d.toString,
      d => Currency.getInstance(d)
    )

  implicit val absoluteUrlColumnType: JdbcType[AbsoluteUrl] with BaseColumnType[AbsoluteUrl] = MappedColumnType
    .base[AbsoluteUrl, String](
      d => d.toString,
      d => AbsoluteUrl.parse(d)
    )

  implicit val hostColumnType: JdbcType[Host] with BaseColumnType[Host] = MappedColumnType
    .base[Host, String](
      d => d.toString,
      d => Host.parse(d)
    )

}

object Repository extends CustomType {

  val sites = TableQuery[Sites]
  val siteProducts = TableQuery[SiteProducts]
  val siteProductPrices = TableQuery[SiteProductSnapshots]

  class Sites(tag: Tag) extends Table[Site](tag, "site") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def host = column[Host]("host", O.Unique)

    def * =
      (id, name, host) <>
        (Site.tupled, Site.unapply)

  }

  class SiteProducts(tag: Tag) extends Table[SiteProduct](tag, "product") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def url = column[AbsoluteUrl]("url", O.Unique)

    def productCode = column[String]("product_code")

    def fetchCron = column[CronExpr]("fetch_cron")

    def siteId = column[Int]("site_id")

    def * =
      (id, name, url, productCode, fetchCron, siteId) <>
        (SiteProduct.tupled, SiteProduct.unapply)

    def site = foreignKey("site", siteId, sites)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Restrict)

  }

  class SiteProductSnapshots(tag: Tag) extends Table[SiteProductSnapshot](tag, "product_price") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def price = column[BigDecimal]("price")

    def currency = column[Option[Currency]]("currency")
    def fetchTime= column[Instant]("fetch_time")

    def productId = column[Int]("product_id")

    def * =
      (id, price, currency, fetchTime, productId) <>
        (SiteProductSnapshot.tupled, SiteProductSnapshot.unapply)

    def product = foreignKey("product", productId, siteProducts)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Restrict)

  }

}

