package com.zbutwialypiernik.scrappify.product

import cats.data.{EitherT, OptionT}
import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.api.v1.product.ProductRequest
import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import com.zbutwialypiernik.scrappify.common.{NotFoundError, Page, ServiceError}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler
import com.zbutwialypiernik.scrappify.site.SiteService
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot
import io.lemonlabs.uri.Host
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class UnsupportedSiteError(host: Host) extends ServiceError(s"Host $host is not supported")

class ProductService(productRepository: SiteProductRepository, siteService: SiteService, siteProductScheduler: SiteProductScheduler)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def findProductById(id: Int): Future[Option[SiteProduct]] = productRepository.database.run(productRepository.findById(id))

  def listPrices(productId: Int, page: Page): Future[Seq[SiteProductSnapshot]] =
    productRepository.listPrices(productId, page)

  def listProducts(siteId: Option[Int] = Option.empty[Int], name: Option[String] = Option.empty[String], page: Page): Future[Seq[SiteProduct]] =
    productRepository.list(siteId, name, page)

  def create(request: ProductRequest): AsyncResult[SiteProduct] = {
    val host = request.url.host

    EitherT.fromOptionF(siteService.findSiteByHost(host), UnsupportedSiteError(host))
      .flatMapF(site => {
        val action = for {
          product <- productRepository.createAndFetch(SiteProduct(0, request.name, request.url, request.productCode, request.fetchCron, site.id))
          _ <- DBIO.from(siteProductScheduler.cronSchedule(product.id, product.fetchCron, product.url).value)
        } yield product

        productRepository.run(action.transactionally)
          .andThen {
            case Success(value) => logger.info(s"Created new product $value")
            case Failure(error) => logger.error(s"Could not create new product from request $request", error)
          }
          .map(Right(_))
      })
  }

  def requestProductPriceRefresh(productId: Int): AsyncResult[Unit] = {
    EitherT.fromOptionF(findProductById(productId), NotFoundError(s"Product with ${productId} does not exists"))
      .flatMap(product => siteProductScheduler.instantSchedule(product.id, product.url))
  }

}
