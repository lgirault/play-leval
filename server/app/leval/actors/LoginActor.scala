package leval.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import scala.collection.immutable.HashMap

/**
  * Created by Loïc Girault on 11/30/16.
  */

object LoginActor {
  case class Join(name : String)
  case class Quit(name : String)
  case object ListRequest
}

import LoginActor._
class LoginActor @Inject() extends Actor
  with ActorLogging {

  var users = HashMap.empty[String, ActorRef]

  def receive: Receive = {
    case ListRequest =>
      users foreach {
        case (u, _) => sender() ! Join(u)
      }

    case Join(name) =>
      users foreach {
        case (_, r) => r ! Join(name)
      }
      users += (name -> sender())

    case Quit(name) =>
      users -= name
      users foreach {
        case (_, r) => r ! Quit(name)
      }

  }
}
