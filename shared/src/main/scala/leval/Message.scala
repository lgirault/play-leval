package leval

import leval.core.{PlayerId, Rules}

case class IdedMessage(user : Int, content : Message)

sealed abstract class Message

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

case class GameDescription
(owner : PlayerId,
 rules : Rules) extends Message

case object ChallengeAccepted extends Message
case class ChallengeDenied(reason : String) extends Message

case class Join(login : String) extends Message
case object Quit extends Message
case object ListUserRequest extends Message


