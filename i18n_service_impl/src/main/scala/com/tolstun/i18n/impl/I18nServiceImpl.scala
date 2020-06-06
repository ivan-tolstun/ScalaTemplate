package com.tolstun.i18n.impl

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision, SystemMaterializer}
import akka.{Done, NotUsed}
import com.tolstun.common.lagom.service.ServiceImpl
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.tolstun.i18n.api.I18nServiceApi
import com.tolstun.i18n.data_access.I18nDataAccessService
import com.tolstun.i18n.dto.I18nDTO._
import com.tolstun.i18n.behaviour.I18nBehaviourImpl._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import play.api.Configuration
import com.softwaremill.macwire.wire

import scala.concurrent.{ExecutionContextExecutor, Future}
import slick.jdbc.MySQLProfile.api.Database


class I18nServiceImpl(db: Database,
                      val configuration: Configuration,
                      val actorSystem: ActorSystem)
                     (implicit val materializer: Materializer) extends I18nServiceApi with ServiceImpl {

  private val log = LoggerFactory.getLogger(classOf[I18nServiceImpl])

  val decider: Supervision.Decider = {
    e: Throwable ⇒
      log.error(e.getLocalizedMessage)
      e.printStackTrace()
      Supervision.Resume
  }

  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  implicit val actorMaterializer: Materializer = SystemMaterializer(actorSystem).materializer

  private lazy val i18nDataAccessService = wire[I18nDataAccessService]


  override def ping: ServiceCall[NotUsed, Done] = Auth.withHeaders {
    case (header, profile, request) => Future.successful(Done)
  }


  override def getI18nDTO: ServiceCall[NotUsed, I18nDTOList] = Auth.withHeaders {
    case (header, profile, request) =>

      i18nDataAccessService
        .getI18nSeq()
        .map(_.flatMap(_.toDTO.toList).toList)
        .map(I18nDTOList)
  }


  override def getTranslationDTO(languageCode: String): ServiceCall[NotUsed, TranslationDTOList] = Auth.withHeaders {
    case (header, profile, request) =>

      i18nDataAccessService
        .getTranslationSeq(languageCode)
        .map(_.map(_.toDTO).toList)
        .map(TranslationDTOList)
  }

}
