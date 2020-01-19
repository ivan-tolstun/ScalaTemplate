package com.tolstun.evaluationService.api

import akka.{Done, NotUsed}
import com.tolstun.common.lagom.serializers.CirceSerializerImpl._
import com.tolstun.common.lagom.serializers.CustomSerializerImpl._
import com.tolstun.common.lagom.service.ServiceApi
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}


trait EvaluationServiceApi extends ServiceApi {

  val serviceName = "evaluation-service"

  def ping: ServiceCall[NotUsed, String]

  def descriptor: Descriptor = {
    namedService().addCalls(

      restCall(Method.GET, servicePath("ping"), ping _),

    )
  }
}
