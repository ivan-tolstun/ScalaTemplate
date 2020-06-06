package com.tolstun.i18n.serializer

import com.tolstun.i18n.dto.I18nDTO._

import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.syntax._


trait I18nDTOSerializer {

  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames


  implicit val i18nDTODecoder: Decoder[I18nDTO] = deriveConfiguredDecoder
  implicit val i18nDTOEncoder: Encoder[I18nDTO] = deriveConfiguredEncoder


  implicit val I18nDTOListDecoder: Decoder[I18nDTOList] = deriveConfiguredDecoder
  implicit val I18nDTOListEncoder: Encoder[I18nDTOList] = deriveConfiguredEncoder


  implicit val translationListDecoder: Decoder[TranslationDTOList] = (c: HCursor) => {

    val decoderPage = Decoder.decodeMap(KeyDecoder.decodeKeyString, (Decoder.decodeJson)).decodeJson(c.value)
    decoderPage.map(_.flatMap { case (page, langJson) =>

      val decoderSection = Decoder.decodeMap(KeyDecoder.decodeKeyString, (Decoder.decodeJson)).decodeJson(c.value)
      decoderSection.map(_.flatMap { case (section, langJson) =>

        val decoderlang = Decoder.decodeMap(KeyDecoder.decodeKeyString, (Decoder.decodeString)).decodeJson(langJson)
        decoderlang.map(_.map {
          case (tag, lang) => TranslationDTO(page, section, tag, lang)
        }).getOrElse(throw new RuntimeException(s"Failed to decode json to ${TranslationDTOList.getClass.getSimpleName}")).toList

      }).getOrElse(throw new RuntimeException(s"Failed to decode json to ${TranslationDTOList.getClass.getSimpleName}")).toList

    })
      .map(_.toList)
      .map(TranslationDTOList)
  }


  implicit val translationListEncoder: Encoder[TranslationDTOList] = {

    implicit class TranslationDTOOpt(val translationList: TranslationDTOList) extends AnyRef {

      def groupBySection: Map[String, Map[String, Json]] = translationList.languages
        .groupBy(_.section)
        .map {
          case (section: String, translationList: List[TranslationDTO]) => {

            val langMap: Map[String, Json] = translationList.map {
              lang => (lang.tag, Json.fromString(lang.value))
            }.toMap

            (section, langMap)
          }
        }

      def groupByPage: Map[String, TranslationDTOList] = translationList.languages
        .groupBy(_.page)
        .map {
          case (page, translationList) => (page, TranslationDTOList(translationList))
        }

    }

    (langRespList: TranslationDTOList) =>
      langRespList
        .groupByPage
        .map { case (page, translationsList) => (page, translationsList.groupBySection) }
        .asJson
  }

}

object I18nDTOSerializerImpl extends I18nDTOSerializer
