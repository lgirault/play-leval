package leval

import leval.core.PlayerId


case class IdedMessage(user : Int, content : Message)
case class CSMessage(user : Int, content : ClientServerMessage)

sealed abstract class Message
sealed abstract class ServerServerMessage extends Message
sealed abstract class ClientServerMessage extends Message

sealed abstract class ConnectRequestMessage extends Message
sealed abstract class ConnectAnswerMessage extends Message

//initial handshake
case class GuestConnect( login : String) extends ConnectRequestMessage
case class Connect
( login : String,
  password : String)
  extends Message
case class ConnectAck(id : PlayerId) extends ConnectAnswerMessage
case class ConnectNack(msg : String) extends ConnectAnswerMessage


object Message {
  case class Join(login : String) extends ClientServerMessage
  case object Quit extends ClientServerMessage

  case object ListUserRequest extends Message
}

