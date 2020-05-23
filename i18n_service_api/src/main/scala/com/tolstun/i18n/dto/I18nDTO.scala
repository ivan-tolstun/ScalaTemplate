package com.tolstun.i18n.dto

object I18nDTO {

  sealed trait Root

  case class I18nDTO(topic: String,
                     tag: String,
                     languageCode: String,
                     defaultTranslation: String,
                     customTranslation: Option[String],
                     isEditable: Boolean) extends Root

  case class I18nDTOList(i18nList: Seq[I18nDTO]) extends Root

  case class TranslationDTO(topic: String, tag: String, value: String) extends Root

  case class TranslationDTOList(languages: List[TranslationDTO]) extends Root

}
