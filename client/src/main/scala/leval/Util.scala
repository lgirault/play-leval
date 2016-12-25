package leval

import org.scalajs.dom._
import org.scalajs.dom.{DOMList, FormData, XMLHttpRequest, html}

/**
  * Created by lorilan on 12/22/16.
  */
object Util {

  implicit class DivOps(val div : html.Div) extends AnyVal {
    def show() : Unit = div.style.display = "block"
    def hide() : Unit = div.style.display = "none"
  }




  implicit class DOMListOps[T](val list : DOMList[T])
    extends AnyVal {

    def iterator: Iterator[T] = new Iterator[T] {
      private [this] var i = 0
      def hasNext : Boolean = i < list.length
      def next(): T = {
        val t = list item i
        i += 1
        t
      }
    }

    def foreach[U]( f : T => U) : Unit =
      iterator foreach f

    def toList : List[T] = {
      var l = List.empty[T]

      for(i <- (list.length-1).to(0, -1))
        l ::= list.item(i)

      l
    }

  }


  def post
  (form : html.Form,
   onSuccess : XMLHttpRequest => Unit,
   onFailure : XMLHttpRequest => Unit ) : Unit = {
    val formData = new FormData(form)

    val xhr = new XMLHttpRequest()
    xhr.open("POST", form getAttribute "action", async = true)
    xhr.onreadystatechange =
      (e: Event) => {
        if(xhr.readyState == XMLHttpRequest.DONE) {
          if(xhr.status == 200) onSuccess(xhr)
          else onFailure(xhr)
        }
      }
    console.info(formData.toString)
    xhr.send(formData)
  }
}
