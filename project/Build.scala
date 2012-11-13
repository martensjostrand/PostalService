import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build{
  val akkaVersion = "2.0"
  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = Project.defaultSettings ++ assemblySettings ++ Seq(
      name := "PostalService",
      organization := "com.netlight.fnnl",
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" % "akka-actor" % akkaVersion,
        "com.typesafe.akka" % "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" % "akka-agent" % akkaVersion,
        "com.typesafe.akka" % "akka-testkit" % akkaVersion % "test",
        "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.1.1",
        "org.scalatest" %% "scalatest" % "1.6.1" % "test",
        "org.mockito" % "mockito-core" % "1.7" % "test",
        "org.slf4j" % "slf4j-simple" % "1.6.1",
        "org.slf4j" % "slf4j-api" % "1.6.1")
    )
  )
}
