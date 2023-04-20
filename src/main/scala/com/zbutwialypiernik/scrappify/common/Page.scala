package com.zbutwialypiernik.scrappify.common

case class PageRequest(index: Int, size: Int) {
  require(index >= 0, "Index must be non negative")
  require(size >= 1, "Size must be greater than zero")

  def offset: Int = index * size

  def toPage[T](items: Seq[T], totalItems: Int): Page[T] =
    Page(index, size, items, totalItems)
}


case class Page[A](index: Int, size: Int, items: Seq[A], totalItems: Int) {
  require(index >= 0, "Index must be non negative")
  require(size >= 1, "Size must be greater than zero")
  require(totalItems >= 0, "Total numbers must be non negative")
  require(totalItems >= items.size, "Total items must be greater than or same number of items")

  def offset: Int = index * size

  final def map[B](f: A => B): Page[B] = Page(this.index, this.size, items.map(f(_)), this.totalItems)

}