package com.tolstun.i18n.data_access

import com.softwaremill.macwire.wire
import com.tolstun.common.constants.ErrorMessages
import com.tolstun.i18n.database.misc.CommonDbAction
import com.tolstun.i18n.database.table.I18nTable
import com.tolstun.i18n.database.model.I18n
import org.slf4j.LoggerFactory
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future


class I18nDataAccessService(db: Database) {

  private val log = LoggerFactory.getLogger(classOf[I18nDataAccessService])

  private lazy val commonDbAction = wire[CommonDbAction]

  private val i18nTable = TableQuery[I18nTable]


  def getI18nSeq(): Future[Seq[I18n.I18n]] = db.run {
    i18nTable.result
  }


  def getTranslationSeq(): Future[Seq[I18n.Translation]] = db.run {
    i18nTable
      .map(_.translation)
      .result
  }

}
