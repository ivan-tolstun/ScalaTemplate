package com.tolstun.i18n.dto

import com.tolstun.common.dto.CirceDTO.CirceSerializable

object I18nDTO {

  sealed trait Root extends CirceSerializable

  case class I18nDTO(languageCode: String,
                     page: String,
                     section: String,
                     tag: String,
                     defaultTranslation: String,
                     customTranslation: Option[String],
                     isEditable: Boolean) extends Root

  case class I18nDTOList(i18nList: Seq[I18nDTO]) extends Root

  case class TranslationDTO(page: String,
                            section: String,
                            tag: String,
                            value: String) extends Root

  case class TranslationDTOList(languages: List[TranslationDTO]) extends Root

}
