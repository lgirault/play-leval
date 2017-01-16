package leval

import leval.core.{Card, User, Rules, Twilight}

sealed abstract class Message
case class IdedMessage(user : Int, content : Message) extends Message

sealed abstract class ConnectRequestMessage extends Message
sealed abstract class ConnectAnswerMessage extends Message

//initial handshake
case class GuestConnect( login : String) extends ConnectRequestMessage
case class Connect
( login : String,
  password : String)
  extends Message
case class ConnectAck(id : User) extends ConnectAnswerMessage
case class ConnectNack(msg : String) extends ConnectAnswerMessage

case class GameDescription
(owner : User,
 rules : Rules) extends Message

sealed abstract class ChallengeAnswer extends Message
case object ChallengeAccepted extends ChallengeAnswer
case class ChallengeDenied(reason : String) extends ChallengeAnswer

case class Join(login : String) extends Message
case object Quit extends Message
case object ListUserRequest extends Message

//game messages
sealed abstract class GameMessage extends Message

sealed abstract class GameInit extends GameMessage
case class RegularGameInit
(twilight: Twilight,
 stars : Seq[User],
 hand : Seq[Card],
 rules : Rules) extends GameInit

case class OSteinGameInit
(stars : Seq[User],
 hands : Seq[Seq[Card]],
 rules : Rules) extends GameInit


