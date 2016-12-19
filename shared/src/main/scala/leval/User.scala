package leval

case class User(name : String, status : UserStatus)

sealed abstract class UserStatus
object UserStatus {
  case object Join extends UserStatus
  case object Quit extends UserStatus
}

