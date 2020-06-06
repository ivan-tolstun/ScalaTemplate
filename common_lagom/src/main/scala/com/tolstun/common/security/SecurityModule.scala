package com.tolstun.common.security

import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

import com.lightbend.lagom.scaladsl.api.LagomConfigComponent
import com.nimbusds.jose.jwk.{KeyUse, RSAKey}
import com.typesafe.config.ConfigFactory
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer.isAnonymous
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer.isAuthenticated
import org.pac4j.core.config.Config
import org.pac4j.core.context.HttpConstants.{AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX}
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.credentials.{Credentials, TokenCredentials}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.{CookieClient, HeaderClient}
import org.pac4j.lagom.jwt.JwtAuthenticatorHelper

import scala.util.Try


object ClientNames {

  val HEADER_CLIENT = "simple_header"
  val HEADER_JWT_CLIENT = "jwt_header"
  val COOKIE_CLIENT = "cookie_header"

}

trait SecurityModule extends LagomConfigComponent {

  private def buildJWK(publicKeyBase64: Option[String], keyID: Option[String]): Option[RSAKey] = {
    Try {
      for (key <- publicKeyBase64; kid <- keyID) yield {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = new X509EncodedKeySpec(Base64.getDecoder.decode(key))
        val publicKey: java.security.interfaces.RSAPublicKey = keyFactory.generatePublic(keySpec).asInstanceOf[java.security.interfaces.RSAPublicKey]
        val jwk: RSAKey = new (RSAKey.Builder)(publicKey).keyID(kid).keyUse(KeyUse.SIGNATURE).build()

        jwk
      }
    }.toOption.flatten
  }


  private val kcConfig = Try(ConfigFactory.load())
  // scala.util.Properties.envOrNone("ENV_TOLSTUN_KEYCLOAK_PUBLIC_KEY")
  private val kc_publicKey =  kcConfig.flatMap(c => Try(c.getString("keycloak.public_key"))).toOption
  // scala.util.Properties.envOrNone("ENV_TOLSTUN_KEYCLOAK_KID")
  private val kc_keyId =  kcConfig.flatMap(c => Try(c.getString("keycloak.kid"))).toOption
  private val jwtConfiguration =
    s"""
      |  signatures = [
      |    {
      |      algorithm = "RS256"
      |      jwk = ${buildJWK(kc_publicKey, kc_keyId).map(_.toJSONString).getOrElse("{}")}
      |    }
      |  ]
    """.stripMargin
  private val jwtConfig = ConfigFactory.parseString(jwtConfiguration)


  lazy val client: HeaderClient = {
    val headerClient = new HeaderClient("AUTHORIZATION_HEADER", new Authenticator[Credentials]() {
      override def validate(credentials: Credentials, webContext: WebContext): Unit = {
        val profile = new CommonProfile()
        profile.setId(credentials.asInstanceOf[TokenCredentials].getToken)
        credentials.setUserProfile(profile)
      }
    })
    headerClient.setName(ClientNames.HEADER_CLIENT)
    headerClient
  }


  lazy val cookieClient: CookieClient = {
    val cookieClient = new CookieClient("auth", new Authenticator[Credentials]() {
      override def validate(credentials: Credentials, webContext: WebContext): Unit = {
        val profile = new CommonProfile()
        profile.setId(credentials.asInstanceOf[TokenCredentials].getToken)
        credentials.setUserProfile(profile)
      }
    })
    cookieClient.setName(ClientNames.COOKIE_CLIENT)
    cookieClient
  }


  lazy val jwtClient: HeaderClient = {
    val headerClient = new HeaderClient
    headerClient.setHeaderName(AUTHORIZATION_HEADER)
    headerClient.setPrefixHeader(BEARER_HEADER_PREFIX)
    headerClient.setAuthenticator(JwtAuthenticatorHelper.parse(jwtConfig))
    headerClient.setName(ClientNames.HEADER_JWT_CLIENT)
    headerClient
  }


  lazy val serviceConfig: Config = {
    val config = new Config(client, jwtClient, cookieClient)
    config.getClients.setDefaultSecurityClients(client.getName)
    config.addAuthorizer("_anonymous_", isAnonymous[CommonProfile])
    config.addAuthorizer("_authenticated_", isAuthenticated[CommonProfile])
    config
  }

}
