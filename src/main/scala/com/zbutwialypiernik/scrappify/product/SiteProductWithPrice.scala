package com.zbutwialypiernik.scrappify.product

import cron4s.expr.CronExpr
import io.lemonlabs.uri.AbsoluteUrl

import java.time.Instant
import java.util.Currency

case class SiteProductWithPrice(id: Int,
                                name: String,
                                url: AbsoluteUrl,
                                code: String,
                                fetchCron: CronExpr,
                                siteId: Int,
                                latestPrice: Option[BigDecimal],
                                currency: Option[Currency],
                                lastUpdate: Option[Instant])

