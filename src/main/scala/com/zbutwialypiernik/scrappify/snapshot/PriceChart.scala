package com.zbutwialypiernik.scrappify.snapshot

import java.time.{Duration, LocalDate}

case class PriceChart(data: Seq[DailyPrice], range: Duration)

case class DailyPrice(day: LocalDate, price: BigDecimal)
