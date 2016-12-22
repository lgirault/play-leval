package leval

import org.scalajs.dom
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.raw.XMLHttpRequest

/**
  * Created by lorilan on 12/22/16.
  */
object Util {

  def post
  ( content : String,
    onSuccess : XMLHttpRequest => Unit,
    onFailure : XMLHttpRequest => Unit ) : Unit = {
    val xhr = new XMLHttpRequest()
    xhr.open("POST", "/", async = true)
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

    xhr.onreadystatechange =
      (e: dom.Event) => {
        if(xhr.readyState == XMLHttpRequest.DONE) {
          if(xhr.status == 200) onSuccess(xhr)
          else onFailure(xhr)
        }
      }
    xhr.send(content)
  }
}
