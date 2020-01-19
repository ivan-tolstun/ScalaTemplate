import sbt._
import Versions._

object Dependencies {

  // diverse
  val macWire = "com.softwaremill.macwire" %% "macros" % macWireVersion % "provided"
  val macWireMacrosAkka = "com.softwaremill.macwire" %% "macrosakka" % macWireVersion % "provided"
  val macWireUtils = "com.softwaremill.macwire" %% "util" % macWireVersion
  val macWireProxy = "com.softwaremill.macwire" %% "proxy" % macWireVersion

  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  val nScalaTime = "com.github.nscala-time" %% "nscala-time" % nScalaTimeVersion

  // logging
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
  var logbackEncoder = ("net.logstash.logback" %% "logstash-logback-encoder" % logbackEncoderVersion).cross(CrossVersion.disabled)
  val slf4j = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % slf4jVersion

  // akka
  val agent = "com.typesafe.akka" %% "akka-agent" % agentVersion

  // alpakka
  val alpakkaKafka = "com.typesafe.akka" %% "akka-stream-kafka" % alpakkaKafkaVersion
  val alpakkaSlick = "com.lightbend.akka" %% "akka-stream-alpakka-slick" % alpakkaVersion

  // akka-quartz-scheduler

  val akkaQuartzScheduler = "com.enragedginger" %% "akka-quartz-scheduler" % akkaQuartzSchedulerVersion

  // Circe
  val circeCore = "io.circe" %% "circe-core" % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeParser = "io.circe" %% "circe-parser" % circeVersion
  val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeVersion

  // Spark
  val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
  val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
  val sparkHive = "org.apache.spark" %% "spark-hive" % sparkVersion
  val sparkExcel = "com.crealytics" %% "spark-excel" % sparkExcelVersion
  val sparkCassandraConnector = "com.datastax.spark" %% "spark-cassandra-connector" % sparkVersion

  // AWS
  val awsSdk = "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion
  val awsSdkS3 = "com.amazonaws" % "aws-java-sdk" % awsSdkVersion

  // Hadoop
  val hadoopAws = "org.apache.hadoop" % "hadoop-aws" % hadoopVersion
  val hadoopClient = "org.apache.hadoop" % "hadoop-client" % hadoopVersion

  // jackson
  val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
  val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
  val jacksonModule = "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion

  // jackson for SPARK
  val jacksonCoreForSpark = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersionForSpark
  val jacksonDatabindForSpark = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersionForSpark
  val jacksonModuleForSpark = "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersionForSpark

  // cats
  val cats = "org.typelevel" %% "cats-core" % catsVersion

  //mysqlDriver
  val mysql = "mysql" % "mysql-connector-java" % mysqlDriverVersion

  //wix-embedded-mysql
  val wix = "com.wix" % "wix-embedded-mysql" % wixVersion % Test

  //embeded kafka
  val embeddedKafka = "io.github.embeddedkafka" %% "embedded-kafka" % embeddedKafkaVersion % Test exclude("org.slf4j", "slf4j-log4j12")

  //slick
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val slickhikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion

  //slick + shapeless
  val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion
  val slickless = "io.underscore" %% "slickless" % slicklessVersion

  //XML
  val xml = "org.scala-lang.modules" %% "scala-xml" % "1.1.1"

  //enumeratum
  val enumeratum = "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion

  // authentication

  val lagomPac4jDeps = Seq(
    "org.pac4j" %% "lagom-pac4j" % lagomPac4jVersion,
    "org.pac4j" % "lagom-pac4j-parent" % lagomPac4jVersion,
    "org.pac4j" % "pac4j-jwt" % pac4jVersion,
    "org.pac4j" % "pac4j-http" % pac4jVersion
  )

  val keyCloakCore = "org.keycloak" % "keycloak-core" % keyCloakVersion
  val keyCloakAdapterCore = "org.keycloak" % "keycloak-adapter-core" % keyCloakVersion

  //excel
  val poi = "org.apache.poi" % "poi-ooxml" % poiVersion

  //aws
  val awsS3 = "com.amazonaws" % "aws-java-sdk-s3" % awsVersion

  val alpakkaS3 = "com.lightbend.akka" %% "akka-stream-alpakka-s3" % alpakkaS3Version

  val alpakkaCSV = "com.lightbend.akka" %% "akka-stream-alpakka-csv" % alpakkaVersion

  val breeze = "org.scalanlp" %% "breeze" % breezeVersion

  val plotlyCore = "org.plotly-scala" %% "plotly-core" % plotlyVersion

}
