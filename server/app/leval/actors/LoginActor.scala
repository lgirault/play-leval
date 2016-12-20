package leval.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.collection.immutable.HashMap
import leval.{IdedMessage, Message}
/**
  * Created by Loïc Girault on 11/30/16.
  */
class LoginActor @Inject() extends Actor
  with ActorLogging {

  var users = HashMap.empty[String, ActorRef]

  def receive: Receive = {
    case Message.ListUserRequest =>
      users foreach {
        case (u, _) => sender() ! IdedMessage(u, Message.Join)
      }

    case msg @ IdedMessage(name, Message.Join) =>
      users foreach {
        case (_, r) => r ! msg
      }
      users += (name -> sender())

    case msg @ IdedMessage(name, Message.Quit) =>
      users -= name
      users foreach {
        case (_, r) => r ! msg
      }

  }
}
