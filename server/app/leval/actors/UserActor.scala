package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import leval.Message

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

/**
  * Created by Loïc Girault on 11/30/16.
  */
import leval.IdedMessage
object UserActor {
  def apply( loginActor : ActorRef,
             out : ActorRef) : Props = Props(new UserActor(loginActor, out))
}

class UserActor
( loginActor : ActorRef,
  out : ActorRef)
  extends Actor with ActorLogging {

  var thisLogin : Option[String] = None

  def receive: Receive = {
    case msg @ IdedMessage(_, _) => //from loginActor
      out ! msg.asJson.noSpaces

    case str : String =>
      decode[IdedMessage](str) match {
        case Right(msg @ IdedMessage(login, Message.Join)) =>
          thisLogin = Some(login)
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
    thisLogin foreach {
      l =>
        loginActor ! IdedMessage(l, Message.Quit)
    }

  }

  loginActor ! Message.ListUserRequest
}
