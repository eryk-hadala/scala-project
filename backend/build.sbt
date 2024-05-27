ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "app"
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.2"
val AkkaHttpVersion = "10.6.1"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
//  "ch.megard" %% "akka-http-cors" % "1.2.0" cross CrossVersion.for3Use2_13,
  "org.mindrot" % "jbcrypt" % "0.4",
  "com.typesafe.slick" %% "slick" % "3.5.1",
  "org.slf4j" % "slf4j-nop" % "2.0.13",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
  "org.xerial" % "sqlite-jdbc" % "3.45.3.0",
  "com.github.jwt-scala" %% "jwt-core" % "10.0.1",
  "com.lihaoyi" %% "upickle" % "3.3.0",
)