package com.zbutwialypiernik.scrappify.support

import com.zbutwialypiernik.scrappify.product.{SiteProduct, SiteProductSnapshot}
import com.zbutwialypiernik.scrappify.scrapper.ScrappingResult
import cron4s.Cron
import io.lemonlabs.uri.AbsoluteUrl

import java.time.Instant
import java.util.Currency


trait CommonParams {

  val validCronOne = Cron.unsafeParse("0 */30 * ? * *")

  val validProduct = SiteProduct(0, "Macbook M1", AbsoluteUrl.parse("https://www.apple.com/product/macbook"), "ABCD1234", validCronOne, 0)

  val validScrappingResult = ScrappingResult(BigDecimal.apply("50"), Some(Currency.getInstance("PLN")), Some("Macbook Air M1/16GB/256GB"), Instant.now())

  val validProductSnapshot = SiteProductSnapshot(0, BigDecimal.apply("50"), Some(Currency.getInstance("PLN")), Instant.now(), validProduct.id)

}
