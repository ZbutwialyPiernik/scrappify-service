package com.zbutwialypiernik.scrappify.api.v1.product

import java.time.Instant

case class ProductWithPrice(id: Int, name: String, code: String, url: String, fetchCron: String, latestPrice: Option[Int], currency: Option[String], lastUpdate: Option[Instant], siteId: Int)
