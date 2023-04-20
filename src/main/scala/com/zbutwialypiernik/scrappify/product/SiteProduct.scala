package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.database.Identifiable
import cron4s.expr.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

// SiteProduct is used to avoid conflicts with Scala Product class
case class SiteProduct(id: Int, name: String, url: AbsoluteUrl, code: String, fetchCron: CronExpr, siteId: Int) extends Identifiable[Int, SiteProduct] {
  override def copyWithId(id: Int): SiteProduct = copy(id = id)

}