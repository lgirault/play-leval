package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import leval.{GameDescription, IdedMessage}

/**
  * Created by lorilan on 12/25/16.
  */
object ChallengeActor {
  def apply(actors : Map[Int, ActorRef]) : Props =
    Props(new ChallengeActor(actors))
}

class ChallengeActor
( actors : Map[Int, ActorRef])
  extends Actor
  with ActorLogging {


  def receive: Receive = {
    case msg @ IdedMessage(challenged, GameDescription(_,_)) =>
      actors(challenged) ! msg

  }

}
