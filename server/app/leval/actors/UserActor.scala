package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import leval._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import leval.core.User
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

  var thisId : Option[User] = None

  var challengeActor : Option[ActorRef] = None

  //we know it's from user since it was received as Json
  def handleMsgFromUser(msg : Message) : Unit =
    msg match {
      case IdedMessage(id, Join(login)) =>
        thisId = Some(User(id, login))
        loginActor ! msg

      case cd @ ChallengeDenied(_) =>
        challengeActor foreach (_ ! cd)
        challengeActor = None

      case ChallengeAccepted =>
        challengeActor foreach (_ ! msg)

      case _ => Logger debug s"$msg received"
    }

  def receive: Receive = {
    case msg @ GameDescription(_,_) => //from ChallengeActor
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
      decode[Message](str) match {
        case Right(msg) => handleMsgFromUser(msg)
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
