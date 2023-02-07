package com.zbutwialypiernik.scrappify.product

import cron4s.expr.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

import java.time.Instant
import java.util.Currency

// SiteProduct is used to avoid conflicts with Scala Product class
case class SiteProduct(id: Int, name: String, url: AbsoluteUrl, productCode: String, fetchCron: CronExpr, siteId: Int)

case class SiteProductSnapshot(id: Int, price: BigDecimal, currency: Option[Currency], fetchTime: Instant, productId: Int)