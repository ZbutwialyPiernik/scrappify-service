package com.zbutwialypiernik.scrappify.common

abstract class ServiceError(val message: String)

case class NotFoundError(override val message: String) extends ServiceError(message)

case class InternalServiceError(override val message: String) extends ServiceError(message)