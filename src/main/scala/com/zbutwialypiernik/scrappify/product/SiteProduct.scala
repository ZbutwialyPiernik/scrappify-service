package com.zbutwialypiernik.scrappify.product

import com.zbutwialypiernik.scrappify.database.Entity
import cron4s.expr.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

// SiteProduct is used to avoid conflicts with Scala Product class
case class SiteProduct(id: Int, name: String, url: AbsoluteUrl, productCode: String, fetchCron: CronExpr, siteId: Int) extends Entity[Int, SiteProduct] {
  override def copyWithId(id: Int): SiteProduct = copy(id = id)

}