package com.zbutwialypiernik.scrappify.config

import pureconfig._
import pureconfig.generic.auto._

class ConfigurationModule(val configSource: ConfigSource) {
  lazy val serviceConfiguration: ServiceConfiguration = configSource.at("scrappify.service").load[ServiceConfiguration].getOrElse(ServiceConfiguration())
  lazy val databaseConfiguration: DatabaseConfiguration = configSource.at("scrappify.database").loadOrThrow[DatabaseConfiguration]
  lazy val schedulerConfiguration: SchedulerConfiguration = configSource.at("scrappify.scheduler").load[SchedulerConfiguration].getOrElse(SchedulerConfiguration())
}

