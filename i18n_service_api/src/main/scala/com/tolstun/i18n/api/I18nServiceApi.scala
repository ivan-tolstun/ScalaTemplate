package com.tolstun.i18n.api

import akka.{Done, NotUsed}
import com.tolstun.common.lagom.serializers.CirceSerializerImpl._
import com.tolstun.common.lagom.serializers.CustomSerializerImpl._
import com.tolstun.i18n.serializer.I18nDTOSerializerImpl._
import com.tolstun.common.lagom.service.ServiceApi
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}
import com.tolstun.i18n.dto.I18nDTO._


trait I18nServiceApi extends ServiceApi {

  val serviceName = "i18n-service"


  def ping: ServiceCall[NotUsed, Done]

  def getI18nDTO: ServiceCall[NotUsed, I18nDTOList]
  def getTranslationDTO: ServiceCall[NotUsed, TranslationDTOList]


  def descriptor: Descriptor = {
    namedService().addCalls(
      restCall(Method.GET, servicePath("ping"), ping _),

      restCall(Method.GET, servicePath("i18n"), getI18nDTO _),
      restCall(Method.GET, servicePath("translation"), getTranslationDTO _)
    )
  }
}
