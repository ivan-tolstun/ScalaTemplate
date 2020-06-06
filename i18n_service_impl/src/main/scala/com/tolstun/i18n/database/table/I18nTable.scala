package com.tolstun.i18n.database.table

import com.tolstun.i18n.database.model.I18n._
import slick.ast.ColumnOption.PrimaryKey
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{MappedProjection, ProvenShape}


class I18nTable(tableTag: Tag) extends ExtendedTable[I18n](tableTag, Some("DEVELOP_DB"), "I18N") {

  def language_code: Rep[String] = column[String]("language_code", PrimaryKey)

  def page: Rep[String] = column[String]("page", PrimaryKey)

  def section: Rep[String] = column[String]("section", PrimaryKey)

  def tag: Rep[String] = column[String]("tag", PrimaryKey)

  def default_translation: Rep[String] = column[String]("default_translation")

  def custom_translation: Rep[Option[String]] = column[Option[String]]("custom_translation")

  def is_editable: Rep[Boolean] = column[Boolean]("is_editable")


  val columns = (language_code, page, section, tag, default_translation, custom_translation, is_editable)

  def * : ProvenShape[I18n] = columns <> ((I18n.apply _).tupled, I18n.unapply)

  def translation: MappedProjection[Translation, (String, String, String, String, Option[String])] =
    (page, section, tag, default_translation, custom_translation) <> ((Translation.apply _).tupled, Translation.unapply)

}

