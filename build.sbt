ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "zip-zap-zio",
    idePackagePrefix := Some("com.github.imcamilo")
  )
