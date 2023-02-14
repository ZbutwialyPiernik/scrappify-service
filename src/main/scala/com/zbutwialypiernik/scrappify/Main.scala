package com.zbutwialypiernik.scrappify

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext

object Main extends App
  with AppContext
  with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("scrappify")
  implicit val executionContext: ExecutionContext = system.dispatcher

  logger.info("Starting up scrappify server")
  databaseModule.sqlDatabase.updateSchema()
  Http()
    .newServerAt("0.0.0.0", configurationModule.serviceConfiguration.port)
    .bindFlow(apiV1Module.routes)

  schedulerModule.scheduler.start()
  //databaseModule.sqlDatabase.close()
}
