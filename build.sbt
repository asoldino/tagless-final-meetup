name := "tagless-final-meetup"

version := "1.0.0"

scalaVersion := "2.12.6"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val commonDependencies = Seq(
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.typelevel" %% "cats-effect" % "0.10.1",
  "io.monix" %% "monix" % "3.0.0-RC1",
  "org.log4s" %% "log4s" % "1.6.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13"
)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

lazy val http4sVersion = "0.19.0-SNAPSHOT"

lazy val web = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies ++= commonDependencies ++ testDependencies ++ web