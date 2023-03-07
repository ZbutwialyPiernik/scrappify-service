package com.zbutwialypiernik.scrappify.snapshot


import com.zbutwialypiernik.scrappify.scrapper.ScrappingResult

import scala.concurrent.Future


class SiteProductSnapshotService(siteProductSnapshotRepository: SiteProductSnapshotRepository) {

  def registerSnapshot(productId: Int, scrappingResult: ScrappingResult): Future[SiteProductSnapshot] =
    siteProductSnapshotRepository.database.run {
      siteProductSnapshotRepository.createAndFetch(SiteProductSnapshot(0, scrappingResult.price, scrappingResult.currency, scrappingResult.name, scrappingResult.fetchTime, productId))
    }

}
