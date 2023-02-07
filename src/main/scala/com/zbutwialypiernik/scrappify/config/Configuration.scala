package com.zbutwialypiernik.scrappify.config

case class DatabaseConfiguration(url: String, user: String, name: String, password: String = "")

case class ServiceConfiguration(port: Int = 9091)

case class SchedulerConfiguration(threads: Int = 10)
