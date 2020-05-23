package com.tolstun.i18n.database.misc

import com.lightbend.lagom.scaladsl.api.transport.{TransportErrorCode, TransportException}
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.slf4j.LoggerFactory


class CommonDbAction  {

  private val log = LoggerFactory.getLogger(classOf[CommonDbAction])

  def recoverWithError[U](errorMessage: => String): PartialFunction[Throwable, Future[U]] = {

    case t: Throwable => {
      log.error(t.getMessage)
      log.error(t.getStackTrace.mkString)
      Future.failed(new TransportException(TransportErrorCode.BadRequest, "%s \n %s".format(errorMessage, t.getLocalizedMessage)))
    }
  }

  def runDbAction[R](db:Database)(action: DBIOAction[R, NoStream, Nothing])(errorMessage: => String): Future[R] = {
    db.run(action).recoverWith(recoverWithError(errorMessage))
  }

}