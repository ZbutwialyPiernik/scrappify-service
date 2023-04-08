package com.zbutwialypiernik.scrappify.api.v1.product

import cron4s.expr.CronExpr
import io.lemonlabs.uri.AbsoluteUrl
import octopus.dsl._

case class ProductRequest(name: String, url: AbsoluteUrl, code: String, fetchCron: CronExpr)

object ProductRequest {

  implicit val productValidator: Validator[ProductRequest] = Validator[ProductRequest]
    .rule[String](_.name, _.trim.nonEmpty, "must be not empty")

}

