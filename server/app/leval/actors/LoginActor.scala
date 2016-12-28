package leval.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef}
import leval.core.PlayerId

import scala.collection.immutable.{HashMap, IntMap}
import leval._
import play.api.Logger
/**
  * Created by Loïc Girault on 11/30/16.
  */
class LoginActor @Inject() extends Actor
  with ActorLogging {

  var usersId : Int = 0
  var pending = Set.empty[String]
  var users = HashMap.empty[Int, (String, ActorRef)]


  def receive: Receive = {
    case GuestConnect(login) =>
      if(pending contains login)
        sender() ! ConnectNack("user already pending")
      else {
        val id = PlayerId(usersId, login)
        usersId += 1
        pending += login
        println(sender())
        sender() ! ConnectAck(id)
      }

    case ListUserRequest =>
      users foreach {
        case (uId, (uLogin, _)) => sender() ! IdedMessage(uId, Join(uLogin))
      }

    case msg @ IdedMessage(uId, Join(uLogin)) =>
      if(pending contains uLogin) {
        users foreach {
          case (_, (l, r)) =>
            Logger.info(s"send $msg to $l")
            r ! msg
        }
        pending -= uLogin
        users += (uId -> (uLogin, sender()))
      }

    case msg @ IdedMessage(name, Quit) =>
      users -= name
      users foreach {
        case (_, (_, r)) => r ! msg
      }

    case msg @ IdedMessage(challenged, GameDescription(challenger, rules)) =>
      val map =
        Map(
          challenged -> users(challenged)._2,
          challenger.uuid -> users(challenger.uuid)._2
        )

      val ca = context actorOf ChallengeActor(map, rules)

      ca ! msg
  }
}
