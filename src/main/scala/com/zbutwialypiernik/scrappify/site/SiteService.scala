package com.zbutwialypiernik.scrappify.site

import com.typesafe.scalalogging.StrictLogging
import com.zbutwialypiernik.scrappify.common.PageRequest
import io.lemonlabs.uri.Host

import scala.concurrent.{ExecutionContext, Future}

class SiteService(val siteRepository: SiteRepository)(implicit executionContext: ExecutionContext) extends StrictLogging {

  def findById(id: Int): Future[Option[Site]] = siteRepository.database.run {
    siteRepository.findById(id)
  }

  def findSiteByHost(host: Host): Future[Option[Site]] = siteRepository.findByHost(host)

  def listSites(name: Option[String], page: PageRequest): Future[Seq[Site]] = siteRepository.listSites(name, page)

}
