name := "Payment-System"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.17"
val AkkaHttpVersion = "10.2.7"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.github.pureconfig" %% "pureconfig" % "0.17.0",
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.19.0-M13",
  "com.softwaremill.sttp.tapir" %% "tapir-json-spray" % "0.19.0-M13",
  "org.typelevel" %% "cats-core" % "2.3.0"
)




