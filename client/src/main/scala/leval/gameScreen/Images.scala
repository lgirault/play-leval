package leval.gameScreen

import leval.Ids
import leval.core._
import org.scalajs.dom.document
/**
  * Created by lorilan on 1/3/17.
  */
object Images {
  private def suit2string(s : Suit) = s match {
    case Club => "clubs"
    case Heart => "hearts"
    case Diamond => "diamonds"
    case Spade => "spades"
  }
  private def rank2string(s : Rank) = s match {
    case Numeric(i) => i.toString
    case Jack => "Jack"
    case Queen => "Queen"
    case King => "King"
  }

  import Joker._
  def cardBaseFileName(c : Card): String = c match {
    case Joker(Black) => "Joker_black"
    case Joker(Red) => "Joker_red"
    case Card(r, s) => s"${rank2string(r)}_of_${suit2string(s)}"
  }

  lazy val basePath : String = (document getElementById Ids.imagesPath).textContent

  def back : String = s"$basePath/cards/back.png"

  def card(c : Card) : String = s"$basePath/cards/${cardBaseFileName(c)}.png"

  def iconBaseFileName(icon: Icon) : String = icon match {
    case PlayCardDirectly => "hand"
    case CreateBeing => "being"
    case EducateBeing => "educate"
    case CreateBeing.Resource(Diamond) => "wit"
    case CreateBeing.Resource(Heart) => "heart"
    case CreateBeing.Resource(Spade) => "weapon"
    case CreateBeing.Resource(Club) => "power"
    case Ok => "ok"
    case Cancel => "cancel"
  }

  sealed abstract class Icon
  case object PlayCardDirectly extends Icon
  case object CreateBeing extends Icon {
    case class Resource(s : Suit) extends Icon
  }
  case object EducateBeing extends Icon
  case object Ok extends Icon
  case object Cancel extends Icon

  def icon(icon: Icon) : String = s"$basePath/icons/${iconBaseFileName(icon)}.png"

}
