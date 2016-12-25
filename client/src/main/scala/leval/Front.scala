package leval

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport




object Front extends js.JSApp with StarList{

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

  def connect(form : html.Form) : Unit =
    Util.post(form,
      xhr => document write xhr.responseText,
      xhr => {
        val div = document.getElementById("errorBox").asInstanceOf[html.Div]
        div.textContent = xhr.responseText
      })
}
