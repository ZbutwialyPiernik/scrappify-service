package com.zbutwialypiernik.scrappify.support

import com.zbutwialypiernik.scrappify.product.SiteProduct
import cron4s.Cron
import faker._
import io.lemonlabs.uri.Url
import org.scalacheck._

trait Fakers {

  implicit def fakeProductGenerator(siteHost: String, siteId: Int): Gen[SiteProduct] =
    for {
      id <- Gen.const(0)
      name <- Gen.const(Faker.default.companyName())
      url <- Gen.const(Url(scheme = "https", host = s"$siteHost.com").toAbsoluteUrl)
      productCode <- Gen.resize(10, Gen.alphaNumStr)
      fetchCron <- Gen.oneOf(Cron.unsafeParse("\"0 */30 * ? * *\""), Cron.unsafeParse("* * 21 */4 *"))
      siteId <- Gen.const(siteId)
    } yield SiteProduct(id, name, url, productCode, fetchCron, siteId)

  def fakeProduct(siteHost: String, siteId: Int) = fakeProductGenerator(siteHost, siteId).sample.get

}
