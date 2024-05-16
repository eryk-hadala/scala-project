ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "app"
  )

lazy val hello = project.in(file("."))
  .settings(
    scalaVersion := "3.3.1",
    libraryDependencies +=
      ("org.typelevel" %% "cats-core" % "x.y.z")
        .cross(CrossVersion.for3Use2_13)
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.2"
val AkkaHttpVersion = "10.6.1"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.mindrot" % "jbcrypt" % "0.4"
)
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.45.0.0"
libraryDependencies += "com.github.jwt-scala" %% "jwt-core" % "10.0.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.1.4"