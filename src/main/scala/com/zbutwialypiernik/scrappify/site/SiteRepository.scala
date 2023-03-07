package com.zbutwialypiernik.scrappify.site
import com.zbutwialypiernik.scrappify.common.Page
import com.zbutwialypiernik.scrappify.database.Repository.{Sites, sites}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.Repository
import io.lemonlabs.uri.Host

import scala.concurrent.Future

class SiteRepository(database: Database) extends Repository[Sites, Site, Int](database) {
  override def table: TableQuery[Sites] = sites

  def findByHost(host: Host): Future[Option[Site]] =
    database.run(sites.filter(_.host === host).result.headOption)

  def listSites(name: Option[String], page: Page): Future[Seq[Site]] =
    database.run(sites
      .drop(page.offset)
      .take(page.size)
      .result)

}
