package leval

/**
  * Created by Loïc Girault on 11/30/16.
  */
import org.scalajs.dom.raw.{CloseEvent, MessageEvent, WebSocket}
import org.scalajs.dom
import org.scalajs.dom.{Element, Event}

import scala.scalajs.js.annotation.JSExport
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.collection.immutable.HashMap




trait UserList {

  @JSExport
  var users = HashMap.empty[String, Element]
  @JSExport
  val ul : Element = dom.document.getElementById("starList")

  @JSExport
  var ws : WebSocket = _


  def processUserMsg(u : User) : Unit = u.status match {
    case UserStatus.Join =>
      val child = dom.document.createElement("li")
      child.textContent = u.name

      users += (u.name -> child)
      ul appendChild child

    case UserStatus.Quit =>
      users get u.name foreach ul.removeChild
      users -= u.name
  }

  @JSExport
  def userListInit(login : String): Unit = {
    ws = new WebSocket(ul.getAttribute("data-ws-url"))

    ws.onmessage = { (event: MessageEvent) =>
      event.data match {
        case msg : String =>
          decode[User](msg) match {
            case Right(u) => processUserMsg(u)
            case Left(err) =>
              dom.console.info(msg + " received  : " + err.getMessage)
          }
        case d =>
          dom.console.info(d.getClass + " received")

      }

    }

    ws.onopen = {
      (_ : Event) =>
        ws send User(login, UserStatus.Join).asJson.noSpaces
    }
  }

}
