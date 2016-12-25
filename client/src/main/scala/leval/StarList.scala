package leval

/**
  * Created by Loïc Girault on 11/30/16.
  */
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import leval.core.PlayerId
import leval.Util._

import scala.collection.immutable.HashMap


trait StarList {

  var users = HashMap.empty[Int, Element]

  val ul : Element = document.querySelector("#starList ul")
  val menu : html.Div = document.querySelector("#starList .dropdown").asInstanceOf[html.Div]
  val defyForm : html.Form = document.getElementById("defyForm").asInstanceOf[html.Form]

  var ws : WebSocket = _

  def showDefyMenu(pid : PlayerId) : Unit = {
    val menu = document.getElementById("defyMenu").asInstanceOf[html.Div]
    menu.show()
    document.getElementById("defiedName").textContent = pid.name
    val defiedInput =
      defyForm.querySelector("input[name=defiedId]").asInstanceOf[html.Input]
    defiedInput.attributes.getNamedItem("value").value = pid.uuid.toString


    val button = defyForm.querySelector("button").asInstanceOf[html.Button]

    button.onclick = (me : MouseEvent) => {
      me.preventDefault()
      Util.post(defyForm,
        onSuccess = xhr =>
          console.log(xhr.responseText),

        onFailure = xhr => ())

    }

  }

  def updateMenu(pid : PlayerId, x : Double, y : Double) : Unit = {
    val c1 = document.getElementById("defyLink").asInstanceOf[html.Link]
    c1.onclick = (me : MouseEvent) => {
      me.preventDefault()
      showDefyMenu(pid)
    }

    val c2 = document.getElementById("pmLink").asInstanceOf[html.Link]
    c2.onclick = (me : MouseEvent) => {
      me.preventDefault()
    }

    menu.show() //show before get width

    menu.style.left = s"${x - menu.clientWidth/2}px"
    menu.style.top = s"${y}px"

  }

  def processUserMsg(u : CSMessage) : Unit = u match {
    case CSMessage(userId, Message.Join(userName)) =>
      val pid = PlayerId(userId, userName)
      val li = document.createElement("li").asInstanceOf[html.LI]
      li.classList add "dropbtn"
      li.textContent = userName

      users += (userId -> li)
      ul appendChild li

      li.onclick = {
        me : MouseEvent =>

          updateMenu(pid, me.clientX, me.clientY)

          window.onclick = {
            me : MouseEvent =>
              if( me.target != li ) {
                menu.hide()
                window.onclick = { me: MouseEvent => () }
              }
          }
      }

    case CSMessage(userId, Message.Quit) =>
      users get userId foreach ul.removeChild
      users -= userId
  }

  val onMessageHandler : MessageEvent => Unit =
    event =>
      event.data match {
        case msg : String =>
          decode[CSMessage](msg) match {
            case Right(u) => processUserMsg(u)
            case Left(err) =>
              console.info(msg + " received  : " + err.getMessage)
          }
        case d =>
          console.info(d.getClass + " received")

      }

  @JSExport
  def starListInit(id : Int, name : String): Unit = {
    ws = new WebSocket(ul.getAttribute("data-ws-url"))

    ws.onmessage = onMessageHandler

    ws.onopen = {
      (_ : Event) =>
        val msg = IdedMessage(id, Message.Join(name)).asJson.noSpaces
        ws send msg
    }

  }

}
