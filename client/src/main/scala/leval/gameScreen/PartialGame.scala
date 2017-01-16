package leval.gameScreen

import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import leval.Ids
import leval.core.Game.StarIdx
import leval.core._
import leval.Util._
import org.scalajs.dom.html.{Div, Image}
import org.scalajs.dom.{Event, document}

import scala.collection.mutable
import scalatags.JsDom.all._

/**
  * Created by lorilan on 1/2/17.
  */




object PartialGame {
  class Star
  (val player : User,
   val majesty : Var[Int],
   val handSize : Var[Int])

}

class PartialGame
(val rules : CoreRules,
 val stars : Array[PartialGame.Star],
 val playerStarIdx : Int,
 val playerHand : Var[Set[Card]] = Var(Star.emptyHand),
 var currentStarIdx : Var[StarIdx] = Var(0),
 val currentPhase: Var[Phase] = Var(InfluencePhase(0)),
 val beings : mutable.Map[Card, StarIdx] = mutable.Map.empty, //being-face -> owner
 val deathRiver: mutable.ListBuffer[Card] = mutable.ListBuffer.empty,
 val currentRound : Var[Int] = Var(1),
 val beingStates : mutable.Map[Card, Being.State] = mutable.Map.empty,
 val lookedCards : mutable.Map[Card, (Suit, Card)] = mutable.Map.empty,
 val revealedCards : mutable.Map[Card, (Suit, Card)] = mutable.Map.empty
){
  val opponentStarIdx : Int = (playerStarIdx + 1) % 2
  def opponent : PartialGame.Star = stars(opponentStarIdx)

  object hand {
    def ++(cs : Iterable[Card]) : Unit =
      playerHand := playerHand.get ++ cs



    def +(c : Card) : Unit =
      playerHand := playerHand.get + c

    def -(c : Card) : Unit =
      playerHand := playerHand.get - c
  }

}




