package com.zbutwialypiernik.scrappify.api.v1.dto

import io.lemonlabs.uri.Host
import octopus.dsl._

case class SiteRequest(name: String, host: Host)

object SiteRequest {

  implicit val productValidator: Validator[SiteRequest] = Validator[SiteRequest]
    .rule[String](_.name, _.trim.nonEmpty, "must be not empty")

}
