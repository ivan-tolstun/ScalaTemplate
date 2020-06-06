package com.tolstun.common.serializers

import com.tolstun.common.serializers.CustomSerializerTypeClass._
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.lightbend.lagom.scaladsl.api.transport.{TransportErrorCode, TransportException}

import scala.reflect.ClassTag


trait CustomSerializer {

  def deserializeFunction[Param](value: String)(implicit decoder: CustomDecoder[Param]): Param =
    decoder.decode(value).fold(
      error => throw new TransportException(TransportErrorCode.BadRequest, error.value),
      msg => msg
    )
  def serializeFunction[Param](value: Param)(implicit encoder: CustomEncoder[Param]): String = encoder.encode(value)

  implicit def customPathSerializer[Param](implicit decoder: CustomDecoder[Param], encoder: CustomEncoder[Param], tag: ClassTag[Param]): PathParamSerializer[Param] ={
    val className = tag.toString.split('$').last.split('.').last
    PathParamSerializer.required[Param](className)(deserializeFunction[Param])(serializeFunction[Param])
  }

}

object CustomSerializerImpl extends CustomSerializer
