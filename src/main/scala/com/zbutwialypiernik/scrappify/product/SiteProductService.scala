package com.zbutwialypiernik.scrappify.product

import cats.data.EitherT
import com.zbutwialypiernik.scrappify.api.v1.dto.ProductRequest
import com.zbutwialypiernik.scrappify.common.{Page, ServiceError}
import com.zbutwialypiernik.scrappify.scheduler.SiteProductScheduler
import com.zbutwialypiernik.scrappify.site.SiteService
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import io.lemonlabs.uri.Host
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

case class UnsupportedSiteError(host: Host) extends ServiceError(s"Host $host is not supported")

class ProductService(productRepository: SiteProductRepository, siteService: SiteService, siteProductScheduler: SiteProductScheduler)(implicit executionContext: ExecutionContext) {

  def findProductById(id: Int): Future[Option[SiteProduct]] = productRepository.database.run(productRepository.findById(id))

  def listPrices(productId: Int, page: Page): Future[Seq[SiteProductSnapshot]] =
    productRepository.listPrices(productId, page)

  def listProducts(siteId: Option[Int] = Option.empty[Int], name: Option[String] = Option.empty[String], page: Page): Future[Seq[SiteProduct]] =
    productRepository.list(siteId, name, page)

  def createProduct(request: ProductRequest): Future[Either[ServiceError, SiteProduct]] = {
    val host = request.url.host

    EitherT.fromOptionF(siteService.findSiteByHost(host), UnsupportedSiteError(host))
      .flatMapF(site => {
        val action = (for {
          id <- productRepository.create(SiteProduct(0, request.name, request.url, request.productCode, request.fetchCron, site.id))
          product <- productRepository.getById(id)
          _ <- DBIO.from(siteProductScheduler.cronScheduleProduct(product))
        } yield product)

        productRepository.run(action.transactionally)
          .map(Right(_))
      }).value
  }


}
