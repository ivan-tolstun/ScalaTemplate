package com.tolstun.i18n.impl

import com.tolstun.common.constants.ConfigPaths
import com.tolstun.common.dto.LoginContext
import com.tolstun.common.dto.UserDTO.UserDTO
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.lightbend.rp.servicediscovery.lagom.scaladsl.LagomServiceLocatorComponents
import com.softwaremill.macwire._
import com.tolstun.i18n.api.I18nServiceApi
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway
import play.api.Configuration
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.filters.cors.CORSComponents
import slick.jdbc.MySQLProfile.api._


class I18nServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new I18nServiceApplication(context) with LagomServiceLocatorComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new I18nServiceApplicationWithCORS(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[I18nServiceApi])
}


abstract class I18nServiceApplication(context: LagomApplicationContext) extends LagomApplication(context) with AhcWSComponents {

  FlywayLoader.load.migrate()

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[I18nServiceApi](wire[I18nServiceImpl])

  lazy val db = Database.forConfig(ConfigPaths.MYSQL)
}


abstract class I18nServiceApplicationWithCORS(context: LagomApplicationContext) extends I18nServiceApplication(context) with CORSComponents {

  override val httpFilters: Seq[EssentialFilter] = Seq(corsFilter)
}


object FlywayLoader {

  val configuration: Configuration = Configuration(ConfigFactory.load())

  def load: Flyway = {

    //db config
    val config = new HikariConfig
    config.setJdbcUrl(configuration.get[String]("flyway.url"))
    config.setUsername(configuration.get[String]("flyway.user"))
    config.setPassword(configuration.get[String]("flyway.password"))
    config.setDriverClassName(configuration.get[String]("flyway.driver"))
    config.setMaximumPoolSize(1)

    // our Flyway migration
    Flyway
      .configure()
      .dataSource(new HikariDataSource(config))
      .load()
  }

}