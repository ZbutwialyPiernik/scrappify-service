package com.zbutwialypiernik.scrappify.site

import com.zbutwialypiernik.scrappify.api.v1.dto.SiteRequest
import com.zbutwialypiernik.scrappify.common.Page
import io.lemonlabs.uri.Host

import scala.concurrent.{ExecutionContext, Future}

class SiteService(val siteRepository: SiteRepository)(implicit executionContext: ExecutionContext) {

  def findById(id: Int): Future[Option[Site]] = siteRepository.database.run {
    siteRepository.findById(id)
  }

  def findSiteByHost(host: Host): Future[Option[Site]] = siteRepository.findByHost(host)

  def listSites(name: Option[String], page: Page): Future[Seq[Site]] = siteRepository.listSites(name, page)

  def createSite(request: SiteRequest): Future[Site] = {
    siteRepository.database.run {
      siteRepository.create(Site(0, request.name, request.host)).flatMap {
        siteRepository.getById
      }
    }
  }

}