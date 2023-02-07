package com.zbutwialypiernik.scrappify.api.v1.dto

case class ProductResponse(id: Int, name: String, producerCode: String, url: String, fetchCron: String, siteId: Int)
