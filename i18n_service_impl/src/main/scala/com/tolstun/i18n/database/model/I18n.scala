package com.tolstun.i18n.database.model

object I18n {

  sealed trait Root


  case class I18n(languageCode: String,
                  page: String,
                  section: String,
                  tag: String,
                  defaultTranslation: String,
                  customTranslation: Option[String],
                  isEditable: Boolean) extends Root


  case class Translation(page: String,
                         section: String,
                         tag: String,
                         defaultTranslation: String,
                         customTranslation: Option[String]) extends Root

}
