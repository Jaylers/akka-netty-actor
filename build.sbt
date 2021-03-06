import sbt.Keys.scalaVersion

name := "akkasocket"
organization := "com.tradition"
version := "0.1"
scalaVersion := "2.12.4"

lazy val common = Seq(
  organization := "com.tradition",
  version := "0.1",
  scalaVersion := "2.12.4"
)

lazy val library = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-remote" % "2.5.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.9" % Test,

  "io.netty" % "netty-all" % "4.1.15.Final",
  "io.spray" %%  "spray-json" % "1.3.3",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.2.3"
)

lazy val myclient = project.settings(libraryDependencies ++= library).dependsOn(Shared)
lazy val myserver = project.settings(libraryDependencies ++= library).dependsOn(Shared)

lazy val Shared = project.in(file("Shared")).settings(common)