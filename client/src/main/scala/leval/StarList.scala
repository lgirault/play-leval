package leval

/**
  * Created by Loïc Girault on 11/30/16.
  */
import org.scalajs.dom._

import io.circe.generic.auto._
import io.circe.parser._
import leval.core.PlayerId
import leval.Util._

import scala.collection.immutable.HashMap

object StarList {

  var users = HashMap.empty[Int, Element]

  val ul : Element = document.querySelector("#starList ul")
  val menu : html.Div = document.querySelector("#starList .dropdown").asInstanceOf[html.Div]
  val challengeForm : html.Form = document.getElementById("challengeForm").asInstanceOf[html.Form]

  var ws : WebSocket = _

  def showDefyMenu(pid : PlayerId) : Unit = {
    val menu = document.getElementById("challengeMenu").asInstanceOf[html.Div]
    menu.show()
    document.getElementById("challengedName").textContent = pid.name
    val challengedInput =
      challengeForm.querySelector("input[name=challengedId]").asInstanceOf[html.Input]
    challengedInput.attributes.getNamedItem("value").value = pid.uuid.toString


    val button = challengeForm.querySelector("button").asInstanceOf[html.Button]

    button.onclick = (me : MouseEvent) => {
      me.preventDefault()
      Util.post(challengeForm,
        onSuccess = xhr =>
          console.log(xhr.responseText),

        onFailure = xhr => ())

    }

  }

  def updateMenu(pid : PlayerId, x : Double, y : Double) : Unit = {
    val c1 = document.getElementById("challengeLink").asInstanceOf[html.Link]
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



  def processMsg(u : IdedMessage) : Unit = u match {
    case IdedMessage(userId, Join(userName)) =>
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

    case IdedMessage(userId, Quit) =>
      users get userId foreach ul.removeChild
      users -= userId

    case IdedMessage(_, GameDescription(owner, rules)) =>
      ChallengedNotification.update(owner, rules)
  }

  val onMessageHandler : MessageEvent => Unit =
    event =>
      event.data match {
        case msg : String =>
          decode[IdedMessage](msg) match {
            case Right(u) => processMsg(u)
            case Left(err) =>
              console.info(msg + " received  : " + err.getMessage)
          }
        case d =>
          console.info(d.getClass + " received")

      }

}
