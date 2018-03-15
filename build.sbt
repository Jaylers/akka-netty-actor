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

  "io.netty" % "netty-all" % "4.1.15.Final"
)

lazy val Client = project.settings(libraryDependencies ++= library).dependsOn(Shared)
lazy val Server =  project.settings(libraryDependencies ++= library).dependsOn(Shared)
lazy val Netto =  project.settings(libraryDependencies ++= library).dependsOn(Shared)

lazy val Shared = project.in(file("Shared")).settings(common)