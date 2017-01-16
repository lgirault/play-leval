package leval.gameScreen

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import leval.core._
import leval.Util._
import org.scalajs.dom.{Event, console, document}
import org.scalajs.dom.html.{Div, Element, Image}
import org.scalajs.dom.raw.Node

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Created by lorilan on 1/12/17.
  */
object Hands {

  var menuMode : MenuMode = PlayCardMenu

  def opponentHand(pg : PartialGame) : Binding[BindingSeq[Div]] = {
    def makeList(handSize: Int) =
      if (handSize == 0) List.empty
      else {
        def divCard(clazz: String) =
          div(`class` := s"$clazz bottom",
            img(`class` := "card", src := Images.back)
          )

        var l = List(divCard("half"))
        for (_ <- 1 until handSize)
          l ::= divCard("cutHalf")
        l
      }

    Binding {
      val hs = pg.opponent.handSize.bind
      Constants(
        makeList(hs) map (_.render):_*
      )
    }

  }

  def playCardMenu(cardWrapper : Div) : TypedTag[Div] =
    div( `class` := "cardMenu",
      div(
        img(`class` := "cardMenuIcon enabled playDirectlyIcon", src := Images.icon(Images.PlayCardDirectly)),
        img(`class` := "cardMenuIcon enabled",
            src := Images.icon(Images.CreateBeing),
            onclick := CreateBeing.displayMenu(cardWrapper) )
      ),
      div(img(`class` := "cardMenuIcon enabled educatebeingIcon", src := Images.icon(Images.EducateBeing)))
    )

  def playerHand(pg : PartialGame) : Binding[BindingSeq[Node]] = {

    def cardToDiv(c : Card, clazz : String) : Div = {
      console.info("div compute for " + c)
      val d =
        div(`class` := s"$clazz handPlayableCard",
          img(`class` := "card", src := Images card c)
        ).render

      hoverCardSetup(c, d)

      d
    }

    def makeList(hand : Set[Card]) =
      if (hand.isEmpty) List.empty
      else hand.tail.foldLeft(List(cardToDiv(hand.head, "half"))) {
        case (acc, c) => cardToDiv(c, "cutHalf") :: acc
      }


    Binding[BindingSeq[Node]] {
      val hand = pg.playerHand.bind

      Constants(makeList(hand): _*)
    }

  }


  def hoverCardSetup(c : Card, cardWrapper : Div) : Unit = {
    cardWrapper.onmouseenter = {
      evt : Event =>

        val menu = menuMode match {
          case CreateBeingMenu => CreateBeing.menu(c)
          case PlayCardMenu => playCardMenu(cardWrapper)
        }

        cardWrapper.insertBefore(menu.render, cardWrapper.firstChild)

    }
    cardWrapper.onmouseleave = {
      evt : Event =>
        val elt = (cardWrapper querySelector ".cardMenu").asInstanceOf[Element]
        elt.delete()

    }
  }


}
