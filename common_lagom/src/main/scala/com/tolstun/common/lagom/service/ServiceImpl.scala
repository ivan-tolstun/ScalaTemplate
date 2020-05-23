package com.tolstun.common.lagom.service

import akka.NotUsed
import com.tolstun.common.constants.ErrorMessages._
import com.tolstun.common.lagom.internal.api.Execution
import com.tolstun.common.security.{ClientNames, SecurityModule}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{Forbidden, RequestHeader, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.pac4j.core.config.Config
import org.pac4j.core.profile.{AnonymousProfile, CommonProfile}
import org.pac4j.lagom.scaladsl.SecuredService
import play.api.Mode.{Dev, Prod}
import play.api.Play
import scala.concurrent.Future


trait ServiceImpl extends ServiceApi with SecuredService with SecurityModule {

  lazy val securityConfig: Config = serviceConfig

  def throwForbiddenError(): ServiceCall[NotUsed, NotUsed] = ServiceCall.apply { _ =>
    throw Forbidden(USER_NOT_AUTHENTICATED)
    Future.successful(NotUsed)
  }


  trait Authenticate[Request, Response] {

    private val anonymousProfile = new AnonymousProfile
    private val mode = Play.routesCompilerMaybeApplication.map(_.mode).getOrElse(Dev)

    def authenticatedWithProfileAndHeader(call: (RequestHeader, CommonProfile, Request) => Future[(ResponseHeader, Response)]):
    ServiceCall[Request, Response] = {

      if (mode != Prod) ServerServiceCall[Request, Response] { (header, request) => call(header, anonymousProfile, request) }

      else authenticate(ClientNames.HEADER_JWT_CLIENT, { profile: CommonProfile =>

        if (profile.getId != anonymousProfile.getId)
          ServerServiceCall[Request, Response] { (headers, request) => call(headers, profile, request) }

        else throw Forbidden(USER_NOT_AUTHENTICATED)
      })
    }

  }


  object Auth {

    def withHeaders[Request, Response](call: (RequestHeader, CommonProfile, Request) => Future[Response]):
    ServiceCall[Request, Response] = {

      val auth: Authenticate[Request, Response] = new Authenticate[Request, Response] {}

      auth.authenticatedWithProfileAndHeader {
        case (headers, profile, request) => call(headers, profile, request).map(response => (ResponseHeader.Ok, response))(Execution.trampoline)
      }
    }

    def withProfile[Request, Response](call: (CommonProfile, Request) => Future[Response]): ServiceCall[Request, Response] = {
      withHeaders { case (_, profile, request) => call(profile, request) }
    }

    def apply[Request, Response](call: Request => Future[Response]): ServiceCall[Request, Response] = {
      withProfile {case (_, request) => call(request)}
    }
  }

}
