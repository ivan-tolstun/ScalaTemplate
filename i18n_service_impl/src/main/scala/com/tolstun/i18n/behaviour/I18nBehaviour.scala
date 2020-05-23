package com.tolstun.i18n.behaviour

import com.tolstun.i18n.database.model.I18n.{I18n, Translation}
import com.tolstun.i18n.dto.I18nDTO.{I18nDTO, I18nDTOList, TranslationDTO}


trait I18nBehaviour {

  implicit class I18nDTOOpt(val i18n: I18n) extends AnyRef {

    def toDTO: Option[I18nDTO] = {
      I18n.unapply(i18n)
        .map(tuple => (I18nDTO.apply _).tupled(tuple))
    }

  }


  implicit class TranslationDTOOpt(val translation: Translation) extends AnyRef {

    def toDTO: TranslationDTO = TranslationDTO(
      translation.topic,
      translation.tag,
      translation.customTranslation.getOrElse(translation.defaultTranslation))

  }


}

object I18nBehaviourImpl extends I18nBehaviour