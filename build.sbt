ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / organization := "com.zbutwialypiernik.scrappify"

val akkaHttpVersion = "10.2.10"
val akkaVersion = "2.6.20"
val scalaTestVersion = "3.2.15"
val testcontainersVersion = "0.40.12"
val slickVersion = "3.4.1"
val awaitilityVersion = "4.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "scrappify"
  ).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka"                 %% "akka-actor"                      % akkaVersion,
    "com.typesafe.akka"                 %% "akka-http"                       % akkaHttpVersion,
    "com.typesafe.akka"                 %% "akka-http-core"                  % akkaHttpVersion,
    "com.typesafe.akka"                 %% "akka-http-spray-json"            % akkaHttpVersion,
    "com.typesafe.akka"                 %% "akka-slf4j"                      % akkaVersion,
    "com.typesafe.akka"                 %% "akka-stream"                     % akkaVersion,
    "com.enragedginger"                 %% "akka-quartz-scheduler"           % "1.9.3-akka-2.6.x",
    "ch.megard"                         %% "akka-http-cors"                  % "1.1.3",
    "com.typesafe.akka"                 %% "akka-slf4j"                      % akkaVersion,
    "com.typesafe.slick"                %% "slick"                           % slickVersion,
    "com.typesafe.slick"                %% "slick-hikaricp"                  % slickVersion,
    "com.typesafe.slick"                %% "slick-codegen"                   % slickVersion,
    "com.github.tminglei"               %% "slick-pg"                        % "0.21.1",
    "net.ruippeixotog"                  %% "scala-scraper"                   % "3.0.0",
    "io.scalaland"                      %% "chimney"                         % "0.6.2",
    "com.github.alonsodomin.cron4s"     %% "cron4s-core"                     % "0.6.1",
    "org.flywaydb"                       % "flyway-core"                     % "9.14.1",
    "org.postgresql"                     % "postgresql"                      % "42.5.3",
    "io.lemonlabs"                      %% "scala-uri"                       % "4.0.3",
    "com.typesafe.scala-logging"        %% "scala-logging"                   % "3.9.5",
    "ch.qos.logback"                     % "logback-classic"                 % "1.4.5",
    "com.softwaremill.macwire"          %% "macros"                          % "2.5.8",
    "org.slf4j"                          % "slf4j-api"                       % "2.0.6",
    "com.github.krzemin"                %% "octopus"                         % "0.4.1",
    "com.github.kagkarlsson"             % "db-scheduler"                    % "11.6",
    "com.github.pureconfig"             %% "pureconfig"                      % "0.17.2",
    "org.awaitility"                     % "awaitility"                      % awaitilityVersion     % Test,
    "org.awaitility"                     % "awaitility"                      % awaitilityVersion     % Test,
    "org.awaitility"                     % "awaitility-scala"                % awaitilityVersion     % Test,
    "com.github.tomakehurst"             % "wiremock-jre8"                   % "2.35.0"              % Test,
    "com.github.spullara.mustache.java"  % "compiler"                        % "0.9.10"              % Test,
    "org.mockito"                        % "mockito-core"                    % "4.11.0"              % Test,
    "org.scalatest"                     %% "scalatest"                       % scalaTestVersion      % Test,
    "org.scalamock"                     %% "scalamock"                       % "5.2.0"               % Test,
    "com.dimafeng"                      %% "testcontainers-scala-scalatest"  % testcontainersVersion % Test,
    "com.dimafeng"                      %% "testcontainers-scala-postgresql" % testcontainersVersion % Test,
    "com.typesafe.akka"                 %% "akka-http-testkit"               % akkaHttpVersion       % Test,
    "com.typesafe.akka"                 %% "akka-stream-testkit"             % akkaVersion           % Test,
    "com.github.javafaker"               % "javafaker"                       % "1.0.2"               % Test,
  )
)

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-language:higherKinds", "-language:postfixOps", "-feature")