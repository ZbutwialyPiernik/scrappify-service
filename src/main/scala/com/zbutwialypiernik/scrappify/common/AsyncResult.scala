package com.zbutwialypiernik.scrappify.common

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}

object AsyncResult {

  type AsyncResult[T] = EitherT[Future, ServiceError, T]

  def apply[T](a: Future[Either[ServiceError, T]]): AsyncResult[T] = EitherT(a)

  def success[T](a: T)(implicit ec: ExecutionContext): AsyncResult[T] =
    EitherT.pure[Future, ServiceError](a)

  def failure[T](error: ServiceError)(implicit ec: ExecutionContext): AsyncResult[T] =
    EitherT.leftT[Future, T](error)

}

