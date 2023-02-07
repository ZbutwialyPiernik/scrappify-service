package com.zbutwialypiernik.scrappify

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import com.zbutwialypiernik.scrappify.api.v1.ApiV1Module
import com.zbutwialypiernik.scrappify.config.ConfigurationModule
import com.zbutwialypiernik.scrappify.database.DatabaseModule
import com.zbutwialypiernik.scrappify.product.SiteProductModule
import com.zbutwialypiernik.scrappify.scheduler.SchedulerModule
import com.zbutwialypiernik.scrappify.scrapper.ScrappingModule
import com.zbutwialypiernik.scrappify.site.SiteModule
import com.zbutwialypiernik.scrappify.snapshot.SiteSnapshotModule
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pureconfig.ConfigSource

import java.time.Clock
import scala.concurrent.ExecutionContext

trait AppContext {

  lazy val configurationModule = new ConfigurationModule(configSource)
  lazy val databaseModule = new DatabaseModule(config, configurationModule.databaseConfiguration)
  lazy val siteModule = new SiteModule(databaseModule.siteRepository)
  lazy val schedulerModule: SchedulerModule = new SchedulerModule(configurationModule.databaseConfiguration, configurationModule.schedulerConfiguration, scrappingModule.scrappingTask)
  lazy val siteProductModule: SiteProductModule = new SiteProductModule(databaseModule.siteProductRepository, siteModule.siteService, schedulerModule.dbSiteProductScheduler)
  lazy val siteProductSnapshotModule: SiteSnapshotModule = new SiteSnapshotModule(databaseModule.siteProductSnapshotRepository)
  lazy val scrappingModule: ScrappingModule = new ScrappingModule(JsoupBrowser(), clock, databaseModule.siteProductRepository, siteProductSnapshotModule.siteProductSnapshotService)
  lazy val apiV1Module = new ApiV1Module(siteProductModule.productService, siteModule.siteService)

  implicit def executionContext: ExecutionContext
  implicit def system: ActorSystem

  def clock: Clock = Clock.systemUTC()
  def config: Config = ConfigFactory.load()
  def configSource: ConfigSource = ConfigSource.fromConfig(config)

}
