package com.tolstun.evaluationService.impl



import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision}
import akka.{Done, NotUsed}
import com.tolstun.common.constants.ErrorMessages
import com.tolstun.common.dto.{Common, LoginContext}
import com.tolstun.common.lagom.service.ServiceImpl
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{RequestHeader, TransportErrorCode, TransportException}
import com.softwaremill.macwire.wire
import com.tolstun.evaluationService.api.EvaluationServiceApi
import io.circe.parser.decode
import io.circe.syntax._
import org.slf4j.LoggerFactory
import play.api.Configuration
import slick.jdbc.MySQLProfile.api.Database
import scala.collection.immutable
import scala.concurrent.{ExecutionContextExecutor, Future}
import slick.jdbc.MySQLProfile.api.Database


class EvaluationServiceImpl(db: Database,
                            val configuration: Configuration,
                            val actorSystem: ActorSystem,
                            val loginContext: LoginContext)
                           (implicit val materializer: Materializer) extends EvaluationServiceApi with ServiceImpl {

  private val log = LoggerFactory.getLogger(classOf[EvaluationServiceImpl])

  // private lazy val orderPlanningDataAccessService = wire[]

  private lazy val user = loginContext.user

  val decider: Supervision.Decider = {
    e: Throwable ⇒
      log.error(e.getLocalizedMessage)
      e.printStackTrace()
      Supervision.Resume
  }

  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  implicit val mat: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider))(actorSystem)


  override def ping: ServiceCall[NotUsed, String] = Auth.withHeaders { case (header, profile, request) =>
    Future.successful("Done")
  }

}
