package com.zbutwialypiernik.scrappify.site

import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.{Page, PageRequest}
import io.lemonlabs.uri.Host

import scala.concurrent.{ExecutionContext, Future}

class SiteService(val siteRepository: SiteRepository)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def findById(id: Int): Future[Option[Site]] = siteRepository.run {
    siteRepository.findById(id)
  }

  def findSiteByHost(host: Host): Future[Option[Site]] = siteRepository.findByHost(host)

  def listSites(name: Option[String], page: PageRequest): Future[Page[Site]] = siteRepository.listSites(name, page)

}
