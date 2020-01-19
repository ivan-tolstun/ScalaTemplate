package com.tolstun.common.dto

import java.time.LocalDate

import com.tolstun.common.dto.Common.CirceSerializable

object UserDTO {

  sealed trait Root extends CirceSerializable

  case class UserDTO(userId: String,
                     login: Option[String] = None,
                     firstName: Option[String] = None,
                     lastName: Option[String] = None,
                     createBy: Option[String] = None,
                     createDate: Option[LocalDate] = None,
                     lastModifiedBy: Option[String] = None,
                     lastModifiedDate: Option[LocalDate] = None) extends Root
}


trait LoginContext {

  import UserDTO._

  def user: UserDTO

}
