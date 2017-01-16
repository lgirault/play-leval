package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Terminated}
import leval.core.{BuryRequest, Game, GameInit, Move, OsteinSelection, User, Rules}
import leval._

/**
  * Created by lorilan on 12/25/16.
  */
object ChallengeActor {
  def apply(actors : Map[Int, (String, ActorRef)],
            rules : Rules) : Props =
    Props(new ChallengeActor(actors, rules))
}

class ChallengeActor
(playersMap : Map[Int, (String, ActorRef)],
 rules : Rules)
  extends Actor
  with ActorLogging {

  def propagate(sender : ActorRef, msg : Any) : Unit =
    for {
      (_, (_, ref)) <- playersMap
      if ref != sender
    } ref ! msg

  def broadcast(msg : Any) : Unit =
    for {
      (_, (_, ref)) <- playersMap
    } ref ! msg

  def playerIds : Iterable[User]=
    for {
      (id, (name, _)) <- playersMap
    } yield User(id, name)

  def actor( id : Int) : ActorRef = playersMap(id)._2


  def receive: Receive = {
    case IdedMessage(challenged, gd @ GameDescription(_,_)) =>
      actor(challenged) ! gd

    case cd @ ChallengeDenied(_) =>
      propagate(sender(), cd)
      self ! PoisonPill

    case ChallengeAccepted =>

      log info "GameStart received, sending GameInit !"

      val (gis, g) = GameInit(playerIds.toSeq, rules)

      val orderedPlayers =
        playersMap.toSeq map (_._2._2)

      (orderedPlayers zip gis) foreach {
        case (ref, gameInit) => ref ! gameInit
      }

      game = g
      context become scheduling(orderedPlayers.toArray)
  }


  var game : Game = _

  def scheduling(orderedPlayers : Array[ActorRef]) : Receive = {

    {
      case m @ (_: Move[_] | _ :OsteinSelection) =>
        log debug m.toString
        propagate(sender(), m)

      case br: BuryRequest =>
        log debug br.toString
        orderedPlayers(br.target.owner) ! br

      case msg @ IdedMessage(id, Quit) =>
        propagate(sender(), msg)

        log debug s"$msg : GameMaker stopping"

        context stop self

      case t @ Terminated(ref) =>
        playersMap foreach {
          case (id, (_, `ref`)) =>
            propagate(sender(), IdedMessage(id, Quit))
          case _ => ()
        }

        log debug s"Terminated($ref) : GameMaker stopping"

        context stop self
    }
  }

}
