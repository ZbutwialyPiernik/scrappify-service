package com.zbutwialypiernik.scrappify.api.v1.product

case class ProductResponse(id: Int, name: String, producerCode: String, url: String, fetchCron: String, siteId: Int)
