package com.zbutwialypiernik.scrappify.common

case class Page(index: Int, size: Int) {
  require(index >= 0, "Index must be non negative")
  require(size >= 1, "Size must be greater than zero")

  def offset = index * size
}
