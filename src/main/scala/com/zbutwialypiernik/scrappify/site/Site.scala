package com.zbutwialypiernik.scrappify.site

import com.zbutwialypiernik.scrappify.database.Entity
import io.lemonlabs.uri.Host

case class Site(id: Int, name: String, host: Host) extends Entity[Int, Site] {
  override def copyWithId(id: Int): Site = copy(id = id)

}
