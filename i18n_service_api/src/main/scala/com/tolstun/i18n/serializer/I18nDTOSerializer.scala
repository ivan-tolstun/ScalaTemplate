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

    val decoderTopic =
      Decoder.decodeMap(KeyDecoder.decodeKeyString, (Decoder.decodeJson)).decodeJson(c.value)

    decoderTopic.map(_.map { case (topic, langJson) =>

      val decoderlang = Decoder.decodeMap(KeyDecoder.decodeKeyString, (Decoder.decodeString)).decodeJson(langJson)

      decoderlang.map(_.map { case (tag, lang) =>
        TranslationDTO(topic, tag, lang)
      })
        .getOrElse(throw new RuntimeException(s"Failed to decode json to ${TranslationDTOList.getClass.getSimpleName}")).toList

    })
      .map(_.toList.flatten)
      .map(TranslationDTOList)
  }

  implicit val translationListEncoder: Encoder[TranslationDTOList] = {

    (langRespList: TranslationDTOList) =>
      langRespList
        .languages
        .groupBy(_.topic)
        .map { case (topic, group) =>

          val langMap = group.map {
            lang => (lang.tag, Json.fromString(lang.value))
          }.toMap

          (topic, langMap)
        }
        .toMap
        .asJson
  }

}

object I18nDTOSerializerImpl extends I18nDTOSerializer
