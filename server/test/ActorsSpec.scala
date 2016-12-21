
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, OptionValues}
import akka.util.Timeout
import leval.{ConnectAck, GuestConnect, IdedMessage, Message}
import leval.actors.{LoginActor, UserActor}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import leval.core.PlayerId

import scala.collection.immutable.HashMap
import scala.concurrent.duration._
/**
  * Created by lorilan on 12/21/16.
  */
class ActorsSpec extends TestKit(ActorSystem("ActorsSpec"))
  with FeatureSpecLike
  with BeforeAndAfterAll
  with OptionValues {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  //  scenario("ChildActor is expected to send SignalReady"){
  //
  //    val parent = TestProbe()
  //
  //    //    parent.childActorOf(Props[ChildActor])
  //    //    parent expectMsg SignalReady
  //
  //    val actorRef = TestActorRef[ChildActor]( Props[ChildActor](), parent.ref, "main")
  //
  //    val t = actorRef.underlyingActor.t
  //
  //    parent.expectMsg((t + 1) seconds,  SignalReady)
  //
  //
  //  }
  //
  //  scenario("MainActor is expected to send SignalReady eventually"){
  //    val parent = TestProbe()
  //    parent.childActorOf(MainActor(2))
  //
  //    parent.expectMsg(11 seconds,  SignalReady)
  //  }

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
    this.expectMsg(ConnectAck(PlayerId(4, "toto")))

    loginActorRef ! IdedMessage(4, Message.Join("toto"))

    existingUsersProbes.foreach {
      _ expectMsg IdedMessage(4, Message.Join("toto"))
    }

  }


  scenario("user actor transmits json encoded join messages to LoginActor"){
    val loginActorProbe = TestProbe()
    val outProbe = TestProbe()
    val userActorRef = TestActorRef[UserActor]( UserActor(loginActorProbe.ref, outProbe.ref))

    loginActorProbe expectMsg Message.ListUserRequest

    //userActorRef.underlyingActor.context become userActorRef.underlyingActor.state(numSignalExpectedBeforeAnswering)

    userActorRef ! IdedMessage(0, Message.Join("toto"))
    loginActorProbe.expectNoMsg()
    outProbe.expectMsg(100 millis, IdedMessage(0, Message.Join("toto")).asJson.noSpaces)

  }
  scenario("user actor transmits non-json messages encoded in json to out"){

    val loginActorProbe = TestProbe()
    val outProbe = TestProbe()
    val userActorRef = TestActorRef[UserActor]( UserActor(loginActorProbe.ref, outProbe.ref))

    loginActorProbe expectMsg Message.ListUserRequest

    userActorRef ! IdedMessage(0, Message.Join("toto")).asJson.noSpaces
    loginActorProbe.expectMsg(100 millis, IdedMessage(0, Message.Join("toto")))
    outProbe.expectNoMsg()
  }
}
