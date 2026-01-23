ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.example"

lazy val root = (project in file("."))
  .settings(
    name := "scala-project",
    libraryDependencies ++= Seq(
      // Test
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,

      // JSON (optionnel)
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",

      // Cats (programmation fonctionnelle)
      "org.typelevel" %% "cats-core" % "2.10.0"
    )
  )
