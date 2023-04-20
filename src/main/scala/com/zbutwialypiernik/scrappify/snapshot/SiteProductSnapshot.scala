package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.database.Identifiable

import java.time.Instant
import java.util.Currency

case class SiteProductSnapshot(id: Int, price: BigDecimal, currency: Option[Currency], name: Option[String], fetchTime: Instant, productId: Int) extends Identifiable[Int, SiteProductSnapshot] {
  override def copyWithId(id: Int): SiteProductSnapshot = copy(id = id)

}
