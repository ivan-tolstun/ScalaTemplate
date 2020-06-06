// based on https://gist.github.com/AlexanderRay/80d9c061ec8ecc68fd0e641f3ce3f1f9

package com.tolstun.common.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{NegotiatedDeserializer, NegotiatedSerializer}
import com.lightbend.lagom.scaladsl.api.deser.{PathParamSerializer, StrictMessageSerializer}
import com.lightbend.lagom.scaladsl.api.transport.{DeserializationException, SerializationException, _}
import com.tolstun.common.dto.CirceDTO.CirceSerializable
import io.circe._
import io.circe.parser._
import io.circe.syntax._

import scala.collection.immutable.Seq
import scala.language.higherKinds
import scala.reflect.ClassTag
import scala.util.control.NonFatal


trait CirceSerializer extends EitherSerializer {


  private def convertToSerializer[A](implicit encoder: Encoder[A],
                                     decoder: Decoder[A]): StrictMessageSerializer[A] = new StrictMessageSerializer[A] {


    private val defaultProtocol = MessageProtocol(Some("application/json"), None, None)
    override val acceptResponseProtocols = Seq(defaultProtocol)


    private class JsValueSerializer(override val protocol: MessageProtocol) extends NegotiatedSerializer[A, ByteString] {
      override def serialize(message: A): ByteString = {
        try {
          ByteString.fromString(encoder(message).toString, protocol.charset.getOrElse("utf-8"))
        } catch {
          case NonFatal(e) => throw SerializationException(e)
        }
      }
    }

    private class JsValueDeserializer(val protocol: MessageProtocol) extends NegotiatedDeserializer[A, ByteString] {
      override def deserialize(wire: ByteString): A = {
        if (protocol.contentType.contains("application/json")) {
          try {
            val value = wire.decodeString(protocol.charset.getOrElse("utf-8"))
            decode(value) match {
              case Left(failure) => throw DeserializationException(failure)
              case Right(data) => data
            }
          } catch {
            case NonFatal(e) => throw DeserializationException(e)
          }
        } else {
          throw UnsupportedMediaType(protocol, defaultProtocol)
        }

      }
    }

    override def serializerForRequest: NegotiatedSerializer[A, ByteString] = new JsValueSerializer(defaultProtocol)

    override def serializerForResponse(acceptedMessageProtocols: Seq[MessageProtocol]): NegotiatedSerializer[A, ByteString] =
      new JsValueSerializer(acceptedMessageProtocols.find(_.contentType.contains("application/json")).getOrElse(defaultProtocol))

    override def deserializer(protocol: MessageProtocol): NegotiatedDeserializer[A, ByteString] = new JsValueDeserializer(protocol)

  }


  implicit def toSerializer[A <: CirceSerializable](implicit encoder: Encoder[A],
                               decoder: Decoder[A]): StrictMessageSerializer[A] = convertToSerializer


  implicit def toEitherSerializer[A, B](implicit leftEncoder: Encoder[A],
                                        leftDecoder: Decoder[A],
                                        rightEncoder: Encoder[B], rightDecoder: Decoder[B]): StrictMessageSerializer[Either[A, B]] = {
    convertToSerializer(
      encodeEither,
      decodeEither
    )
  }


  implicit def circeJsonToSerializer(implicit encoder: Encoder[Json],
                                     decoder: Decoder[Json]): StrictMessageSerializer[Json] = convertToSerializer


  def deserializeFunction[Param <: CirceSerializable](value: String)
                                (implicit decoder: Decoder[Param]): Param = decode[Param](value).fold(
    error => throw new TransportException(TransportErrorCode.BadRequest, error.getLocalizedMessage),
    msg => msg
  )


  def serializeFunction[Param <: CirceSerializable](value: Param)
                              (implicit encoder: Encoder[Param]): String = value.asJson.noSpaces


  implicit def circePathSerializer[Param <: CirceSerializable](implicit decoder: Decoder[Param],
                                          encoder: Encoder[Param],
                                          tag: ClassTag[Param]): PathParamSerializer[Param] = {
    val className = tag.toString.split('$').last.split('.').last
    PathParamSerializer.required[Param](className)(deserializeFunction[Param])(serializeFunction[Param])
  }

}

object CirceSerializerImpl extends CirceSerializer
