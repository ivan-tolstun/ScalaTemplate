import Dependencies.{flywayCore, _}
import Versions._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}


organization in ThisBuild := "com.tolstun"
version in ThisBuild := "1.0.0"


val tolstunDockerRepository = Some("docker.io")
val tolstunDockerUsername   = Some("ivantolstun")


lazy val root = (project in file("."))
  .aggregate(
    i18n_service_impl, i18n_service_impl
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
      enumeratum,
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


lazy val i18n_service_api = (project in file("i18n_service_api"))
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(common, common_lagom)


lazy val i18n_service_impl = (project in file("i18n_service_impl"))
  .enablePlugins(LagomScala, SbtReactiveAppPlugin)
  .settings(
    aggregate in Docker := false,
    scalaVersion := defaultScalaVersion,
    packageName in Docker := "i18n-service",
    dockerRepository := tolstunDockerRepository,
    dockerUsername := tolstunDockerUsername,
    libraryDependencies ++= Seq(
      macWire,
      cats,
      filters,
      scalaTest, lagomScaladslTestKit,
      agent, akkaQuartzScheduler,
      mysql, slick, slickhikari, shapeless, slickless, flywayCore,
      nScalaTime,
      breeze,
      plotlyCore
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(i18n_service_api, common, common_lagom)


// ** disable persistence (Cassandra) **
lagomCassandraEnabled in ThisBuild := false
// ** do not delete database files on start **
lagomCassandraCleanOnStart in ThisBuild := false

// ** disable message broker (Kafka)  **
lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "localhost:9092"
// ** disable service locator broker (Kafka)  **
// lagomServiceLocatorEnabled in ThisBuild := true


// dependency graph
filterScalaLibrary := false // include scala library in output
dependencyDotFile := file("dependencies.dot") //render dot file to `./dependencies.dot`


lazy val dockerCommand_fontConfigUpdate = dockerCommands ++= Seq(
  Cmd("USER", "root"),
  //setting the run script executable
  ExecCmd("RUN", "/bin/sh", "-c", "apk --update add fontconfig ttf-dejavu"),
  Cmd("USER", "demiourgos728")
)

lazy val dockerCommand_as_root = dockerCommands ++= Seq(
  Cmd("USER", "root")
)