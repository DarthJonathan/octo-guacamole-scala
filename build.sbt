name := """akka-microservice"""

version := "1.0"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-camel" % "2.5.32",
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe" % "config" % "1.3.1",
    "org.apache.activemq" % "activemq-camel" % "5.8.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.scalatest" %% "scalatest" % "3.2.12" % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)

lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"
