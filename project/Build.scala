import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build{
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
        "com.typesafe.akka" % "akka-actor" % "2.0",
        "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.1.1", 
        "org.specs2" %% "specs2" % "1.12.2" % "test")
    )
  )
}
