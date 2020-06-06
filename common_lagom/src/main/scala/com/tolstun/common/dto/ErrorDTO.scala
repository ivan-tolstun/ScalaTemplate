package com.tolstun.common.dto

import com.tolstun.common.dto.CirceDTO.CirceSerializable

object ErrorDTO {

  case class ErrorMessage(value: String) extends CirceSerializable
}
