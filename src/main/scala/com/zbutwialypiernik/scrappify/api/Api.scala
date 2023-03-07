package com.zbutwialypiernik.scrappify.api

import akka.http.scaladsl.server._
import com.zbutwialypiernik.scrappify.api.v1.common.JsonSupport

trait Api extends JsonSupport
  with CustomDirectives {

  def routes: Route

}