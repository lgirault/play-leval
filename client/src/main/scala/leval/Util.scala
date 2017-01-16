package leval

import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.{DOMList, Event, FormData, XMLHttpRequest}
import org.scalajs.dom.html._

/**
  * Created by lorilan on 12/22/16.
  */
object Util {

  type MouseEventHandler = MouseEvent => Unit

  implicit class ElementOps(val elt : Element) extends AnyVal {
    def show() : Unit = elt.style.display = "block"
    def showFlex() : Unit = elt.style.display = "flex"
    def hide() : Unit = elt.style.display = "none"
    def delete() : Unit =
      elt.parentNode removeChild elt
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
  (form : Form,
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
    //console.info(formData.toString)
    xhr.send(formData)
  }
}
