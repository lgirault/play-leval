package leval


case class IdedMessage(user : String, content : Message)
case class CSMessage(user : String, content : ClientServerMessage)

sealed abstract class Message
sealed abstract class ClientServerMessage extends Message

object Message {
  case object Join extends ClientServerMessage
  case object Quit extends ClientServerMessage

  case object ListUserRequest extends Message
}

