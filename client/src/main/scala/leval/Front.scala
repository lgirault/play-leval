package leval

import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, XMLHttpRequest, html}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object Front extends js.JSApp with StarList{

  def main(): Unit = ()

  @JSExport
  def loginInit(): Unit = {
    val button = dom.document.querySelector("#loginForm button").asInstanceOf[html.Button]

    val login = dom.document.querySelector("#loginForm input[name^=login]").asInstanceOf[html.Input]

    button.onclick = {
      (me : MouseEvent) =>
        me.preventDefault()
        connect("login="+login.value)
    }
  }

  def connect(login : String) : Unit = {
    val xhr = new XMLHttpRequest()
    xhr.open("POST", "/", async = true)
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

    xhr.onreadystatechange =
      (e: dom.Event) => {
        if(xhr.readyState == XMLHttpRequest.DONE) {
          if(xhr.status == 200) {
            dom.document write xhr.responseText
          }
          else {
           val div = dom.document.getElementById("errorBox").asInstanceOf[html.Div]
            div.textContent = xhr.responseText
          }
        }
    }
    xhr.send(login)
  }
}
