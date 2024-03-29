ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "PursuitProtocol"
  )

//scalaSource in Compile := baseDirectory.value / "src" / "main" / "scala"
Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
//test / fork := true
run / fork := true

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "31.1-jre",
  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0" % Test
)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % Test
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.20.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.20.0"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.32", // Replace with the latest version
  "ch.qos.logback" % "logback-classic" % "1.2.6" // Replace with the latest version
)

Compile / mainClass := Some("Main")
//run / mainClass := Some("Main")

val jarName = "PursuitProtocol.jar"
assembly / assemblyJarName := jarName

// Merging strategies
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case PathList("org", "apache", "logging", "log4j", xs @ _*) => MergeStrategy.first
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}