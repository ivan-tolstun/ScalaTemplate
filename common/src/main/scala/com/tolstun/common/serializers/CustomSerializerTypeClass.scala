package com.tolstun.common.serializers

import com.tolstun.common.dto.Common.ErrorMessage

object CustomSerializerTypeClass {

  trait CustomEncoder[T] {
    def encode(value: T): String
  }

  trait CustomDecoder[T] {
    def decode(value: String): Either[ErrorMessage, T]
  }

}

