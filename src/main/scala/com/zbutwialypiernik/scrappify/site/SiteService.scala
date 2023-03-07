package com.zbutwialypiernik.scrappify.site

import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.api.v1.product.SiteRequest
import com.zbutwialypiernik.scrappify.common.Page
import io.lemonlabs.uri.Host

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SiteService(val siteRepository: SiteRepository)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def findById(id: Int): Future[Option[Site]] = siteRepository.database.run {
    siteRepository.findById(id)
  }

  def findSiteByHost(host: Host): Future[Option[Site]] = siteRepository.findByHost(host)

  def listSites(name: Option[String], page: Page): Future[Seq[Site]] = siteRepository.listSites(name, page)

  def createSite(request: SiteRequest): Future[Site] = {
    siteRepository.database.run {
      siteRepository.createAndFetch(Site(0, request.name, request.host))
    }.andThen {
      case Success(value) => logger.info(s"Created new site $value")
      case Failure(error) => logger.error(s"Could not create new site from request $request", error)
    }
  }

}
