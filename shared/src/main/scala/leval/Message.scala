package leval

import leval.core.{PlayerId, Rules}

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

case class GameDescription
(owner : PlayerId,
 rules : Rules) extends Message


object Message {
  case class Join(login : String) extends ClientServerMessage
  case object Quit extends ClientServerMessage

  case object ListUserRequest extends Message

//  implicit val encoder = new Encoder[CoreRules] {
//    def apply(cr : CoreRules) : Json =
//      Json.fromString(cr.id)
//  }
//
//  implicit val decoder = new Decoder[CoreRules] {
//    def apply(c: HCursor): Decoder.Result[CoreRules] = {
//      val fail = Left(DecodingFailure("failure", c.history))
//       val mkFail = (_ : Any) => fail
//
//      c.focus.fold[Decoder.Result[CoreRules]](
//        fail, mkFail, mkFail,
//        {
//          case "antares" => Right(Antares)
//          case "helios" => Right(Helios)
//          case "sinnlos" => Right(Sinnlos)
//          case _ => fail
//        }, mkFail,mkFail
//      )
//    }
//  }
}

