package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.product.SiteProductSnapshot

import java.time.Instant
import java.util.Currency
import scala.concurrent.{ExecutionContext, Future}

class SiteProductSnapshotService(siteProductSnapshotRepository: SiteProductSnapshotRepository)(implicit executionContext: ExecutionContext) {

  def registerSnapshot(productId: Int, price: BigDecimal, currency: Option[Currency], fetchTime: Instant): Future[SiteProductSnapshot] =
    siteProductSnapshotRepository.database.run {
      siteProductSnapshotRepository.create(SiteProductSnapshot(0, price, currency, fetchTime, productId))
        .flatMap(id => siteProductSnapshotRepository.getById(id))
    }


}
