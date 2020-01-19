package com.tolstun.evaluationService.impl

import com.tolstun.common.constants.ConfigPaths
import com.tolstun.common.dto.LoginContext
import com.tolstun.common.dto.UserDTO.UserDTO
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.lightbend.rp.servicediscovery.lagom.scaladsl.LagomServiceLocatorComponents
import com.softwaremill.macwire._
import com.tolstun.evaluationService.api.EvaluationServiceApi
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.filters.cors.CORSComponents
import slick.jdbc.MySQLProfile.api._


class EvaluationServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new EvaluationServiceApplication(context) with LagomServiceLocatorComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new EvaluationServiceApplicationWithCORS(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[EvaluationServiceApi])
}


abstract class EvaluationServiceApplication(context: LagomApplicationContext) extends LagomApplication(context) with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[EvaluationServiceApi](wire[EvaluationServiceImpl])
  lazy val loginContext: LoginContext = new LoginContext {
    override def user: UserDTO = UserDTO(userId = "f6722a9b-2f4a-448a-8115-8fc8c8fe402e")
  }
  lazy val db = Database.forConfig(ConfigPaths.MYSQL)
}


abstract class EvaluationServiceApplicationWithCORS(context: LagomApplicationContext) extends EvaluationServiceApplication(context) with CORSComponents {

  override val httpFilters: Seq[EssentialFilter] = Seq(corsFilter)

  override lazy val loginContext: LoginContext = new LoginContext {
    override def user: UserDTO = UserDTO(userId = "f6722a9b-2f4a-448a-8115-8fc8c8fe402e")
  }
}