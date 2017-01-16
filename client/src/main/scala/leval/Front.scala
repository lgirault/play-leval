package leval

import org.scalajs.dom._
import com.thoughtworks.binding.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import io.circe.generic.auto._
import io.circe.syntax._
import StarList._
import leval.core.User
import leval.gameScreen.GameScreen


object Front extends js.JSApp {

  def main(): Unit = ()

  @JSExport
  def loginInit(): Unit = {
    val button = document.querySelector("#loginForm button").asInstanceOf[html.Button]

    val loginForm = document.getElementById("loginForm").asInstanceOf[html.Form]

    button.onclick = {
      (me : MouseEvent) =>
        me.preventDefault()
        connect(loginForm)
    }
  }

  private var ws : WebSocket = _

  def send(msg : Message) : Unit= {
    ws send msg.asJson.noSpaces
  }

  var self : User = User(1337, "titi")

  @JSExport
  def starListInit(id : Int, name : String): Unit = {
    ws = new WebSocket(ul.getAttribute("data-ws-url"))

    ws.onmessage = onMessageHandler

    ws.onopen = {
      (_ : Event) =>
        send( IdedMessage(id, Join(name)) )
    }

    self = User(id, name)
  }

  def connect(form : html.Form) : Unit =
    Util.post(form,
      xhr => document write xhr.responseText,
      xhr => {
        val div = document.getElementById("errorBox").asInstanceOf[html.Div]
        div.textContent = xhr.responseText
      })



  @JSExport
  def test() : Unit = {

    GameScreen.init()
  }
}
