package leval.controllers

import leval.{GameDescription, IdedMessage}
import leval.core.{CoreRules, User, Rules}
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

/**
  * Created by lorilan on 12/25/16.
  */
object ChallengeForm {

  implicit val coreRulesFormatter : Formatter[CoreRules] =
    new Formatter[CoreRules] {

      override val format = Some(("format.coreRules", Nil))


      def unbind(key: String, value: CoreRules) : Map[String, String] =
        Map(key -> value.id)

      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], CoreRules] =
        data get key flatMap CoreRules.apply match {
          case Some(cr) => Right(cr)
          case None =>
            Left(List(FormError(key, s"error.coreRules")))
        }
    }

  //import play.api.data.format.Formats.booleanFormat
  implicit val checkBoxBooleanFormat: Formatter[Boolean] = new Formatter[Boolean] {

    override val format = Some(("format.boolean", Nil))

    def bind(key: String, data: Map[String, String]) = {
      Right(data.getOrElse(key, "")).right.flatMap {
        case "on" => Right(true)
        case "" => Right(false)
        case _ => Left(Seq(FormError(key, "error.boolean", Nil)))
      }
    }

    def unbind(key: String, value: Boolean) = Map(key -> (if(value) "on" else ""))
  }



  def wrap
  (cr : CoreRules,
   ostein : Boolean, allowMulligan : Boolean,
   nedemone : Boolean, janus : Boolean,
   challengedId : Int,
   playerId : Int, playerName : String) : IdedMessage =
    IdedMessage(challengedId,
      GameDescription(User(playerId, playerName),
        Rules(cr, ostein, allowMulligan, nedemone, janus)))

  def unwrap(msg : IdedMessage) = msg match {
    case IdedMessage(challengedId,
    GameDescription(User(playerId, playerName),
    Rules(cr, ostein, allowMulligan, nedemone, janus))) =>
      Some((cr, ostein, allowMulligan, nedemone, janus,
        challengedId, playerId, playerName))
    case _ => None
  }


  val instance : Form[IdedMessage] = Form(
    mapping (
      "rule" -> of[CoreRules],
      "ostein" -> of[Boolean],
      "allowMulligan" -> of[Boolean],
      "nedemone" -> of[Boolean],
      "janus" -> of[Boolean],
      "challengedId" -> number,
      "playerId" -> number,
      "playerName" -> text
    )(wrap)(unwrap)
  )

}
