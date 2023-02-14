package com.zbutwialypiernik.scrappify.snapshot


import java.time.Instant
import java.util.Currency
import scala.concurrent.{ExecutionContext, Future}

class SiteProductSnapshotService(siteProductSnapshotRepository: SiteProductSnapshotRepository)(implicit executionContext: ExecutionContext) {

  def registerSnapshot(productId: Int, price: BigDecimal, currency: Option[Currency], name: Option[String], fetchTime: Instant): Future[SiteProductSnapshot] =
    siteProductSnapshotRepository.database.run {
      siteProductSnapshotRepository.createAndFetch(SiteProductSnapshot(0, price, currency, name, fetchTime, productId))
    }


}
