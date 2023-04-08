package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.api.v1.product.ProductWithPrice
import com.zbutwialypiernik.scrappify.common.{Page, PageRequest}
import com.zbutwialypiernik.scrappify.database.Repository.{SiteProducts, siteProductPrices, siteProducts}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.Repository
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.{ExecutionContext, Future}

class SiteProductRepository(database: Database)(implicit executionContext: ExecutionContext) extends Repository[SiteProducts, SiteProduct, Int](database) {

  override def table: TableQuery[SiteProducts] = siteProducts

  def list(siteId: Option[Int], name: Option[String], pageRequest: PageRequest): Future[Page[SiteProduct]] = database.run {
    for {
      products <- siteProducts
        .filterOpt(siteId)((a, b) => a.siteId === b)
        .drop(pageRequest.offset)
        .take(pageRequest.size)
        .result
      total <- siteProducts.size.result
    } yield pageRequest.toPage(
      products,
      total
    )
  }

  def findProductWithPrice(productId: Int): Future[Option[Product]] = database.run {
    for {
      (productQuery) <- siteProducts
        .filter(_.id === productId)
        .joinLeft(siteProductPrices)
        .on(_.id === _.productId)
        .result
        .headOption
    } yield (productQuery)
  }
    /*sql"""select * from product
           where id = $productId

        """.as[(String)]*/

  /*}.map(
    product =>
      ProductWithPrice(
        product.id,
        product.name,
        product.code,
        product.url.toString(),
        product.fetchCron.toString,
        Option.empty,
        Option.empty,
        Option.empty,
        product.siteId
      )
  )*/

  //left join product_prices on product.id product_prices.id

  def listPrices(productId: Int, pageRequest: PageRequest): Future[Seq[SiteProductSnapshot]] =
    database.run(siteProductPrices
      .filter(_.id === productId)
      .drop(pageRequest.offset)
      .take(pageRequest.size)
      .result)

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = database.run(a)

}

