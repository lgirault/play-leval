package leval.gameScreen

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.dom
import leval.Ids
import leval.core._
import leval.Util._
import org.scalajs.dom.document

/**
  * Created by lorilan on 1/12/17.
  */
object GameScreen {

  var game : PartialGame =
    new PartialGame(Sinnlos,
      Array(new PartialGame.Star(User(42, "toto"), Var(25), Var(10)),
        new PartialGame.Star(User(1337, "titi"), Var(25), Var(10))),
      1)
  game.playerHand := Set(C(0, King, Heart), C(0, Queen, Spade), C(0, Numeric(5), Club))
  game.opponent.handSize := 3


  def initLanguageDependantNodes() : Unit = {
    val elts = document getElementsByClassName "messages"
    elts.foreach {
      n =>
        val attr = n.attributes getNamedItem "msgId"
        val id = s"messages_${attr.value}"
        val txt = (document getElementById id).textContent
        n.textContent = txt
    }
  }

  def init() : Unit = {
    dom.render(document getElementById Ids.gameStatus, LeftColumn.context(game))
    dom.render(document getElementById Ids.opponentScore,
      LeftColumn.starInfos(game, game.opponentStarIdx))
    dom.render(document getElementById Ids.playerScore,
      LeftColumn.starInfos(game, game.playerStarIdx))
    dom.render(document getElementById Ids.opponentHand, Hands.opponentHand(game))
    dom.render(document getElementById Ids.playerHand, Hands.playerHand(game))

    dom.render(document getElementById Ids.tmpBeingWrapper, CreateBeing.panel())

    initLanguageDependantNodes()
  }


//  def doHightlightTargets(origin : CardOrigin): Unit = {
//    val highlighteds =
//      if(createBeeingPane.isOpen) createBeeingPane.targets(origin.card)
//      else if(educateBeingPane.isOpen) origin.card match {
//        case c : C => educateBeingPane.targets(c)
//        case j : J =>
//          val resources = educateBeingPane.being.resources
//          educateBeingPane.targets(j) filter {
//            t => (resources get t.pos).isEmpty
//          }
//      }
//      else {
//        val highlighteds0 : Seq[CardDropTarget] =
//          Target(oGame.game, origin.card) flatMap {
//            case SelfStar => Seq(playerStarPanel)
//            case OpponentStar => Seq(opponentStarPanel)
//            case Source => Seq(deck)
//            case DeathRiver => Seq(riverPane)
//            case OpponentSpectrePower =>
//              opponentSpectrePower
//            case TargetBeingResource(s, sides) =>
//              targetBeingResource(s,sides)
//            case _ => Seq()
//          }
//        origin match{
//          case CardOrigin.Hand(_, _) =>
//            createBeeingPane.createBeingLabel +: highlighteds0
//          case _ => highlighteds0
//        }
//      }
//    highlighteds foreach (_.activateHighlight())
//    highlightableRegions = highlighteds
//  }

}
