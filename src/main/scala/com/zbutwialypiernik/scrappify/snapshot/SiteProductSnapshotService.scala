package com.zbutwialypiernik.scrappify.snapshot


import com.zbutwialypiernik.scrappify.common.{AsyncResult, Page, PageRequest}
import com.zbutwialypiernik.scrappify.scrapper.ScrappingResult

import java.time.temporal.ChronoUnit
import java.time.{Clock, Duration, LocalDate}
import scala.concurrent.{ExecutionContext, Future}


class SiteProductSnapshotService(clock: Clock, siteProductSnapshotRepository: SiteProductSnapshotRepository)(implicit executionContext: ExecutionContext) {

  def registerSnapshot(productId: Int, scrappingResult: ScrappingResult): Future[SiteProductSnapshot] =
    siteProductSnapshotRepository.run {
      siteProductSnapshotRepository.createAndFetch(SiteProductSnapshot(0, scrappingResult.price, scrappingResult.currency, scrappingResult.name, scrappingResult.fetchTime, productId))
    }

  def list(productId: Int, page: PageRequest): Future[Page[SiteProductSnapshot]] =
    siteProductSnapshotRepository.list(productId, page)


  def getPriceGraph(productId: Int, duration: Duration): Future[PriceChart] = {
    val dayDuration = duration.toDays
    val today = LocalDate.now(clock)
    val firstDay = if (duration.isZero || duration.isNegative) None else Some(today.minus(dayDuration, ChronoUnit.DAYS))
    siteProductSnapshotRepository.run {
      siteProductSnapshotRepository.retrieveDailyLatestPriceInRange(productId, firstDay, today)
        .map(PriceChart(_, duration.truncatedTo(ChronoUnit.DAYS)))
    }
  }

}
