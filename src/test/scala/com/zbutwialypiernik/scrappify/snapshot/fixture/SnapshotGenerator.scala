package com.zbutwialypiernik.scrappify.snapshot.fixture

import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import com.zbutwialypiernik.scrappify.snapshot.SiteProductSnapshot

import java.util.Currency

trait SnapshotGenerator {
  self: CommonDataGenerators =>
  def sampleSnapshot(productId: Int, price: Int, currency: Currency, month: Int = 1, day: Int = 1, hour: Int = 1): SiteProductSnapshot =
    SiteProductSnapshot(0, price, Some(currency), Option.empty, instantOf(month = month, day = day, hour = hour), productId)

}
