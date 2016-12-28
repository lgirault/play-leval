package leval

import leval.core.PlayerId
import org.scalajs.dom.{MouseEvent, console, document, html}
import leval.Util._
/**
  * Created by lorilan on 12/26/16.
  */
object ChallengePanel {

 val challengeForm : html.Form = document.getElementById("challengeForm").asInstanceOf[html.Form]


  def display(pid : PlayerId) : Unit = {
    val panel = document.getElementById("challengePanel").asInstanceOf[html.Div]
    panel.show()
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



}
