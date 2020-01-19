import Dependencies._
import Versions._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}


organization in ThisBuild := "com.tolstun"
version in ThisBuild := "1.00.0"


val tolstunDockerRepository = Some("docker.io")
val tolstunDockerUsername = Some("ivantolstun")


lazy val root = (project in file("."))
  .aggregate(
    evaluation_service_impl, evaluation_service_impl
  )
  .settings(
    crossScalaVersions := List()
  )


lazy val common = (project in file("common"))
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    target := file(s"common/target_custom/$defaultScalaVersion/"),
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      circeCore,
      circeGenericExtras,
      circeParser,
      cats,
      slick,
      enumeratum,
      shapeless,
      slickless,
      macWire
    ) ++ lagomPac4jDeps
  )


lazy val common_lagom = (project in file("common_lagom"))
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer,
      macWire
    )
  ).dependsOn(common)


lazy val evaluation_service_api = (project in file("evaluation_service_api"))
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(common, common_lagom)


lazy val evaluation_service_impl = (project in file("evaluation_service_impl"))
  .enablePlugins(LagomScala, SbtReactiveAppPlugin)
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    packageName in Docker := "evaluation-service",
    dockerRepository := tolstunDockerRepository,
    dockerUsername := tolstunDockerUsername,
    libraryDependencies ++= Seq(
      macWire,
      filters,
      scalaTest,
      lagomScaladslTestKit,
      cats,
      slick,
      mysql,
      slickhikari,
      agent,
      nScalaTime,
      akkaQuartzScheduler,
      breeze,
      plotlyCore
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(evaluation_service_api, common, common_lagom)


// disable persistence (Cassandra)
lagomCassandraEnabled in ThisBuild := false
// do not delete database files on start
lagomCassandraCleanOnStart in ThisBuild := false
// disable message broker (Kafka)
lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "localhost:9092"

// disable service locator broker (Kafka)
//lagomServiceLocatorEnabled in ThisBuild := false