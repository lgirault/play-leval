package leval
package gameScreen

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import leval.Util._
import leval.core.{Target => _, _}
import org.scalajs.dom.document
import org.scalajs.dom.html.{Div, Image}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._


/**
  * Created by lorilan on 1/11/17.
  */
object CreateBeing {

  val tmpBeing : Var[TempBeing] = Var(TempBeing())


  def placeCard(c : Card, place : Target) : MouseEventHandler  = {
    _ =>
      val tb = tmpBeing.get

      for( prevCard <- tb get place )
        GameScreen.game.hand + prevCard

      tmpBeing := tb + (place -> c)
      GameScreen.game.hand - c

  }

  def cancel : MouseEventHandler = {
    _ =>
      GameScreen.game.hand ++ tmpBeing.get.cards
      tmpBeing := TempBeing()
      Hands.menuMode = PlayCardMenu

  }

  def panel() : Binding[Div] = {

    def imgOrDiv(oc : Option[Card]) =
      oc match {
        case Some(c) => img(`class` := "card", src := Images.card(c))
        case None => div(`class` := "cardBox" )
      }


    Binding {
      val tb = tmpBeing.bind
      div( `class` := "beingPane",
        div(`class`:= "top", imgOrDiv(tb get Diamond) ),
        div(imgOrDiv(tb get Heart),
          imgOrDiv(tb.face),
          imgOrDiv(tb get Spade)),
        div(
          div( `class` := "card", img(src := Images.icon(Images.Ok))),
          imgOrDiv(tb get Club),
          div(`class` := "card", img(src := Images.icon(Images.Cancel))))

      ).render
    }
  }

  def menu(c : Card) : TypedTag[Div] = {
    val tgts = targets(c)

    def tgtToIcon(t : Target) = Images.icon(t match {
      case Face => Images.CreateBeing
      case Resource(s) => Images.CreateBeing.Resource(s)
    })

    def tgtToKV(t : Target) = {
      val modifiers : List[Modifier] =
        if(tgts contains t)
          List(`class` := "cardMenuIcon enabled", onclick := placeCard(c, t) )
        else
          List(`class` := "cardMenuIcon disabled" )

      t -> img( (src := tgtToIcon(t)) :: modifiers :_* )
    }

    val imgs =  Target.values.foldLeft(Map.empty[Target, TypedTag[Image]])( _ + tgtToKV(_))

    div(`class` := "cardMenu",
      div(imgs(Resource(Diamond))),
      div(
        imgs(Resource(Heart)),
        imgs(Face),
        imgs(Resource(Spade))
      ),
      div(imgs(Resource(Club)))
    )
  }

  def displayMenu(cardWrapperClicked : Div) : MouseEventHandler = {
    _ =>
      val pane = (document getElementById "createBeingPane").asInstanceOf[Div]
      pane.show()

      Hands.menuMode = CreateBeingMenu
      cardWrapperClicked.onmouseleave(null)
      cardWrapperClicked.onmouseenter(null)
  }

  def defaultPos(c : Card) : Target =
    c match {
      case Card(_ : Face, _) | Joker(_) => Face
      case Card(_, s) => Resource(s)
    }

  def targets(c : Card ): Seq[Target] = {
    val being = tmpBeing.get
    val rules = GameScreen.game.rules

    val allowedTiles : Seq[Target] =
      c match {
        case Joker(_)
          if being.resources.values exists (c2 => c2.isInstanceOf[J] && c2 != c) => Seq()
        case _ => Suit.values.foldLeft(List(defaultPos(c))) {
          case (l, pos) =>
            if(rules.validResource(being.face, being.resources, c, pos))
              Resource(pos) :: l
            else l
        }
      }

    (being.face, c) match {
      case (Some(Card(lover@(King | Queen), fsuit)), Card(r : Face, s))
        if fsuit == s && rules.otherLover(lover) == r =>
        Resource(Heart) +: allowedTiles
      case _ => allowedTiles
    }
  }



}

object Target {
  def values = Face :: (Suit.values map Resource.apply)
}
sealed abstract class Target
case object Face extends Target
case class Resource(s : Suit) extends Target

case class TempBeing(face : Option[Card] = None, resources : Map[Suit, Card] = Map()) {

  @inline
  def get(s : Suit) : Option[Card] = resources get s

  def get(tgt : Target) : Option[Card] = tgt match {
    case Face => face
    case Resource(s) => get(s)
  }

  def +(kv : (Target, Card)) : TempBeing = kv match {
    case (Face, c) => copy(face = Some(c))
    case (Resource(s), c) =>  copy(resources = resources + (s -> c))
  }

  def cards : List[Card] = face ++: resources.values.toList

}