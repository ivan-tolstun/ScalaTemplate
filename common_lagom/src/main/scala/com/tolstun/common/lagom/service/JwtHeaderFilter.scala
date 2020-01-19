package com.tolstun.common.lagom.service

import com.tolstun.common.constants.ErrorMessages._
import com.lightbend.lagom.scaladsl.api.transport._
import com.tolstun.common.security.{ClientNames, SecurityModule}
import com.typesafe.config.ConfigFactory
import org.pac4j.core.client.Client
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile.CommonProfile
import org.pac4j.lagom.scaladsl.LagomWebContext
import play.api.Mode.{Dev, Prod}
import play.api.{Configuration, Play}

import scala.util.Try

class JwtHeaderFilter(val serviceName: String) extends HeaderFilter with SecurityModule {

  override def configuration: Configuration = Configuration(ConfigFactory.load())


  def transformClientRequest(request: RequestHeader): RequestHeader = {
    //println(s"transformClientRequest: $request")
    request
  }


  def transformServerRequest(request: RequestHeader): RequestHeader = {
    //println(s"transformServerRequest: $request")

    def checkIsLocalCall() = {

      val suffix = "_SERVICE_HOST"
      val envName = serviceName.toUpperCase().replace("-", "_") + suffix
      val currentHost = request.getHeader("host")
      val envHost = sys.env.get(envName)

      val agentPassed = request.getHeader("user-agent").contains("AHC/2.0")
      val hostPassed = (for (h1 <- currentHost; h2 <- envHost) yield h1 == h2).getOrElse(false)

      println("envName: " + envName)
      println("currentHost: " + currentHost)
      println("envHost: " + envHost)
      println("hostPassed: " + hostPassed)
      println("agentPassed: " + agentPassed)

      hostPassed && agentPassed
    }

    val mode = Play.maybeApplication.map(_.mode).getOrElse(Dev)

    if ((mode == Prod) && !checkIsLocalCall()) {

      val profile = Try {
        val clients = serviceConfig.getClients
        val defaultClient = clients.findClient(ClientNames.HEADER_JWT_CLIENT).asInstanceOf[Client[Credentials, CommonProfile]]
        val context = new LagomWebContext(request)
        val credentials = defaultClient.getCredentials(context)
        Option(defaultClient.getUserProfile(credentials, context))
      }.getOrElse(None)

      if (profile.isEmpty) {
        throw Forbidden(USER_NOT_AUTHENTICATED)
      }

    }

    request
  }


  def transformServerResponse(response: ResponseHeader, request: RequestHeader): ResponseHeader = {
    //  println(s"transformServerResponse - request: $request")
    //  println(s"transformServerResponse - response: $response")
    response
  }


  def transformClientResponse(response: ResponseHeader, request: RequestHeader): ResponseHeader = {
    //  println(s"transformClientResponse - request: $request")
    //  println(s"transformClientResponse - response: $response")
    response
  }

}


object JwtHeaderFilter {
  def apply(serviceName: String): JwtHeaderFilter = new JwtHeaderFilter(serviceName)
}