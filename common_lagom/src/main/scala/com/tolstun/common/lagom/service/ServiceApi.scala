package com.tolstun.common.lagom.service

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}
import com.lightbend.lagom.scaladsl.api.Service._


trait ServiceApi extends Service {

  def serviceName: String
  def servicePath(suffix: String): String = s"/api/$serviceName/$suffix"

  def throwForbiddenError(): ServiceCall[NotUsed, NotUsed]

  def namedService(): Descriptor = Service
    .named(serviceName)
    .withExceptionCalls()
    .withAutoAcl(true)
    .withCORS()
    .withHeaderFilter(JwtHeaderFilter(serviceName))


  implicit class DescriptorOps(val d: Descriptor) extends AnyRef {
    def withCORS(): Descriptor =
      d.withAcls(
        ServiceAcl.forMethodAndPathRegex(Method.OPTIONS, servicePath(".*"))
      )

    def withEndPointServiceCalls(): Descriptor =
      d.addCalls(
      )

    def withTestServiceCalls(): Descriptor =
      d.addCalls(
      )

    def withExceptionCalls(): Descriptor =
      d.addCalls(
        restCall(Method.GET, "/api/error/forbidden", throwForbiddenError _)
      )

  }

}
