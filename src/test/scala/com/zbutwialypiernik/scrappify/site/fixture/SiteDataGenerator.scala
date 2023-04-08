package com.zbutwialypiernik.scrappify.site.fixture

import com.zbutwialypiernik.scrappify.api.v1.product.SiteRequest
import com.zbutwialypiernik.scrappify.fixture.CommonDataGenerators
import io.lemonlabs.uri.Host

class SiteDataGenerator {
  self: CommonDataGenerators =>

  def sampleSiteRequest() = {
    val host = sampleHost()
    SiteRequest(host.toString, host)
  }

  def sampleHost(): Host = Host.parse(faker.company().name())

}
