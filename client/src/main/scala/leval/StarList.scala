package leval

/**
  * Created by Loïc Girault on 11/30/16.
  */
import org.scalajs.dom.raw.{MessageEvent, WebSocket}
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, MouseEvent, html}

import scala.scalajs.js.annotation.JSExport
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import leval.core.PlayerId

import scala.collection.immutable.HashMap

trait StarList {

  var users = HashMap.empty[Int, Element]

  val ul : Element = dom.document.querySelector("#starList ul")
  val menu : html.Div = dom.document.querySelector("#starList .dropdown").asInstanceOf[html.Div]

  @JSExport
  var ws : WebSocket = _




  def updateMenu(name : String, x : Double, y : Double) : Unit = {
    val c1 = dom.document.getElementById("defyLink").asInstanceOf[html.Link]
    c1.href = s"#defy?$name"

    val c2 = dom.document.getElementById("pmLink").asInstanceOf[html.Link]
    c2.href = s"#pm?$name"

    menu.classList add "show" //show before get width

    menu.style.left = s"${x - menu.clientWidth/2}px"
    menu.style.top = s"${y}px"

  }

  def hideMenus() : Unit = {
//    val dds = dom.document.getElementsByClassName("dropdown")
//
//    for(i <- 0 until dds.length){
//      val d = dds(i).asInstanceOf[Element]
//      d.classList remove "show"
//    }
    menu.classList remove "show"
  }

  def processUserMsg(u : CSMessage) : Unit = u match {
    case CSMessage(userId, Message.Join(userName)) =>
      val li = dom.document.createElement("li").asInstanceOf[html.LI]
      li.classList add "dropbtn"
      li.textContent = userName

      users += (userId -> li)
      ul appendChild li

      li.onclick = {
        me : MouseEvent =>

          updateMenu(userName, me.clientX, me.clientY)

          dom.window.onclick = {
            me : MouseEvent =>
              if( me.target != li ) {
                hideMenus()
                dom.window.onclick = { me: MouseEvent => () }
              }
          }
      }

    case CSMessage(userId, Message.Quit) =>
      users get userId foreach ul.removeChild
      users -= userId
  }

  @JSExport
  def starListInit(id : Int, name : String): Unit = {
    ws = new WebSocket(ul.getAttribute("data-ws-url"))

    ws.onmessage = { (event: MessageEvent) =>
      event.data match {
        case msg : String =>
          decode[CSMessage](msg) match {
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
        val msg = IdedMessage(id, Message.Join(name)).asJson.noSpaces
        ws send msg
    }
  }

}
