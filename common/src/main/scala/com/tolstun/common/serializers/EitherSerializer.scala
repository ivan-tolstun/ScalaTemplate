package com.tolstun.common.serializers

import io.circe.syntax._
import io.circe.{Decoder, Encoder}


trait EitherSerializer {

  def encodeEither[A, B](implicit encoderA: Encoder[A], encoderB: Encoder[B]): Encoder[Either[A, B]] = {
    o: Either[A, B] =>
      o.fold(_.asJson, _.asJson)
  }

  def decodeEither[A, B](implicit decoderA: Decoder[A], decoderB: Decoder[B]): Decoder[Either[A, B]] =
    decoderA.map(Left.apply) or decoderB.map(Right.apply)

}

object EitherImpl extends EitherSerializer
