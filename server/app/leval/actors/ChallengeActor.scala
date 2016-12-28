package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import leval.core.Rules
import leval.{ChallengeAccepted, ChallengeDenied, GameDescription, IdedMessage}

/**
  * Created by lorilan on 12/25/16.
  */
object ChallengeActor {
  def apply(actors : Map[Int, ActorRef],
            rules : Rules) : Props =
    Props(new ChallengeActor(actors, rules))
}

class ChallengeActor
( actors : Map[Int, ActorRef],
  rules : Rules)
  extends Actor
  with ActorLogging {

  def propagate(sender : ActorRef, msg : Any) : Unit =
    for {
      (_, ref) <- actors
      if ref != sender
    } ref ! msg

  def receive: Receive = {
    case IdedMessage(challenged, gd @ GameDescription(_,_)) =>
      actors(challenged) ! gd

    case cd @ ChallengeDenied(_) =>
      propagate(sender(), cd)
      self ! PoisonPill

    case ChallengeAccepted =>
          ???
  }

}
