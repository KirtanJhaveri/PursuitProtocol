ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "PursuitProtocol"
  )

//scalaSource in Compile := baseDirectory.value / "src" / "main" / "scala"
Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
test / fork := true
run / fork := true

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "31.1-jre",
  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0" % Test
)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % Test

Compile / mainClass := Some("Main")
run / mainClass := Some("Main")

val jarName = "PursuitProtocol.jar"
assembly / assemblyJarName := jarName

// Merging strategies
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}