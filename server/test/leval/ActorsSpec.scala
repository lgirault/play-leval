package leval


import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import io.circe.generic.auto._
import io.circe.syntax._
import leval.actors.{LoginActor, UserActor}
import leval.core.User
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, OptionValues}

import scala.collection.immutable.HashMap
import scala.concurrent.duration._
/**
  * Created by lorilan on 12/21/16.
  */
class ActorsSpec extends TestKit(ActorSystem("leval.ActorsSpec"))
  with FeatureSpecLike
  with BeforeAndAfterAll
  with OptionValues {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  scenario("on new connection login actor transmit join to other userActors "){

    val existingUsersProbes = Seq(TestProbe(), TestProbe(), TestProbe())

    val existingUsers =
      existingUsersProbes.zipWithIndex.foldLeft(HashMap.empty[Int, (String, ActorRef)]){
        case (m, (p, i))=> m + (i -> ("u"+i, p.ref))
      }

    val loginActorRef = TestActorRef[LoginActor](Props[LoginActor])

    loginActorRef.underlyingActor.usersId = 4
    loginActorRef.underlyingActor.users = existingUsers

    loginActorRef.!(GuestConnect("toto"))(testActor)
    this.expectMsg(ConnectAck(User(4, "toto")))

    loginActorRef ! IdedMessage(4, Join("toto"))

    existingUsersProbes.foreach {
      _ expectMsg IdedMessage(4, Join("toto"))
    }

  }


  scenario("user actor transmits json encoded join messages to LoginActor"){
    val loginActorProbe = TestProbe()
    val outProbe = TestProbe()
    val userActorRef = TestActorRef[UserActor]( UserActor(loginActorProbe.ref, outProbe.ref))

    loginActorProbe expectMsg ListUserRequest

    //userActorRef.underlyingActor.context become userActorRef.underlyingActor.state(numSignalExpectedBeforeAnswering)

    userActorRef ! IdedMessage(0, Join("toto"))
    loginActorProbe.expectNoMsg()
    outProbe.expectMsg(100 millis, IdedMessage(0, Join("toto")).asJson.noSpaces)

  }
  scenario("user actor transmits non-json messages encoded in json to out"){

    val loginActorProbe = TestProbe()
    val outProbe = TestProbe()
    val userActorRef = TestActorRef[UserActor]( UserActor(loginActorProbe.ref, outProbe.ref))

    loginActorProbe expectMsg ListUserRequest

    userActorRef ! IdedMessage(0, Join("toto")).asJson.noSpaces
    loginActorProbe.expectMsg(100 millis, IdedMessage(0, Join("toto")))
    outProbe.expectNoMsg()
  }
}
