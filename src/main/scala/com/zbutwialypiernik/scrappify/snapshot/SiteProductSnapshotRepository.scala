package com.zbutwialypiernik.scrappify.snapshot

import com.zbutwialypiernik.scrappify.common.{Page, PageRequest}
import com.zbutwialypiernik.scrappify.database.Repository.{SiteProductSnapshots, siteProductSnapshots}
import com.zbutwialypiernik.scrappify.database.TextSearchPostgresProfile.api._
import com.zbutwialypiernik.scrappify.database.Repository

import java.time.{Instant, LocalDate}
import scala.concurrent.{ExecutionContext, Future}


class SiteProductSnapshotRepository(database: Database)(implicit executionContext: ExecutionContext) extends Repository[SiteProductSnapshots, SiteProductSnapshot, Int](database) {
  override def table = siteProductSnapshots

  def list(productId: Int, pageRequest: PageRequest): Future[Page[SiteProductSnapshot]] =
    database.run(
      paginate(pageRequest,
        siteProductSnapshots
          .filter(_.productId === productId)
      )
    )

  def retrieveDailyLatestPriceInRange(productId: Int, startDateOption: Option[LocalDate], end: LocalDate): DBIO[Seq[DailyPrice]] =
    siteProductSnapshots
      .filter(_.productId === productId)
      .filter(_.fetchTime.in(retrieveBiggestDatesInRange(productId, startDateOption, end)))
      .sortBy(_.fetchTime)
      .map(xd => (xd.fetchTime.asColumnOf[LocalDate], xd.price) <> (DailyPrice.tupled, DailyPrice.unapply))
      .result

  private def retrieveBiggestDatesInRange(productId: Int, startDateOption: Option[LocalDate], end: LocalDate): Query[Rep[Option[Instant]], Option[Instant], Seq] =
    siteProductSnapshots
      .filter(_.productId === productId)
      .filter(_.fetchTime.asColumnOf[LocalDate] <= end)
      .filterOpt(startDateOption)((a, start) => a.fetchTime.asColumnOf[LocalDate] >= start)
      .groupBy(_.fetchTime.asColumnOf[LocalDate])
      .map(_._2.map(_.fetchTime).max)


}
