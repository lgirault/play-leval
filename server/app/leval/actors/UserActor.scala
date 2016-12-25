package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import leval._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import leval.core.PlayerId
import play.api.Logger

/**
  * Created by Loïc Girault on 11/30/16.
  */
import leval.IdedMessage
object UserActor {
  def apply( loginActor : ActorRef,
             out : ActorRef) : Props =
    Props(new UserActor(loginActor, out))
}

class UserActor
( loginActor : ActorRef,
  out : ActorRef)
  extends Actor with ActorLogging {

  var thisId : Option[PlayerId] = None

  var challengeActor : Option[ActorRef] = None


  def receive: Receive = {
    case msg @ IdedMessage(_, GameDescription(_,_)) => //from ChallengeActor
      challengeActor match {
        case Some(_) =>
          sender() ! ChallengeDenied("already challenged")
        case None =>
          challengeActor = Some(sender())
          out ! msg.asJson.noSpaces
      }

    case msg @ IdedMessage(_, _) => //from loginActor
      out ! msg.asJson.noSpaces

    case str : String =>
      decode[IdedMessage](str) match {
        case Right(msg @ IdedMessage(id, Join(login))) =>
          Logger.info(s"receiving $msg as json")
          thisId = Some(PlayerId(id, login))
          loginActor ! msg

        case Right(IdedMessage(login, status)) =>
          println(s"user $status received")
        case Left(_) => ()
      }

    case x =>
      log info s"something received : $x"
  }

  override def postStop(): Unit = {
    log info "postStop !"
    thisId foreach {
      pid =>
        loginActor ! IdedMessage(pid.uuid, Quit)
    }

  }

  loginActor ! ListUserRequest
}
