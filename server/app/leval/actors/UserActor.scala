package leval.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import leval.UserStatus
//import play.api.libs.json.{JsObject, JsString, JsValue, Json}


import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

/**
  * Created by Loïc Girault on 11/30/16.
  */
import leval.User
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
    case LoginActor.Join(u) =>
      out ! User(u, UserStatus.Join).asJson.noSpaces
    case LoginActor.Quit(u) =>
      out ! User(u, UserStatus.Quit).asJson.noSpaces

    case j : Json =>
      log info s"json $j received"

    case str : String =>
      decode[User](str) match {
        case Right(User(login, UserStatus.Join)) =>
          thisLogin = Some(login)
          loginActor ! LoginActor.Join(login)

        case Right(User(login, status)) =>
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
        loginActor ! LoginActor.Quit(l)
    }

  }

  loginActor ! LoginActor.ListRequest
}
