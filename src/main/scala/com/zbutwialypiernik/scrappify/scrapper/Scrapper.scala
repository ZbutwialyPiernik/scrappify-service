package com.zbutwialypiernik.scrappify.scrapper

import com.zbutwialypiernik.scrappify.common.AsyncResult.AsyncResult
import io.lemonlabs.uri.AbsoluteUrl

import java.time.Instant
import java.util.Currency

case class ScrappingResult(price: BigDecimal,
                           currency: Option[Currency],
                           name: Option[String],
                           fetchTime: Instant)

private trait Scrapper {

  def execute(url: AbsoluteUrl): AsyncResult[ScrappingResult]

  def supports(url: AbsoluteUrl): Boolean

}