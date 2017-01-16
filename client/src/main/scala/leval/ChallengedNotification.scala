package leval

import org.scalajs.dom._
import leval.core.{User, Rules}
import Util.ElementOps
/**
  * Created by lorilan on 12/25/16.
  */
object ChallengedNotification {

  lazy val wrapper : html.Div = (document getElementById "challengedNotification").asInstanceOf[html.Div]

  def init(): Unit ={
    val challengeAccepted =
      (wrapper querySelector "#challenge_accepted").asInstanceOf[html.Button]

    challengeAccepted.onclick = (me : MouseEvent) => {
      me.preventDefault()
      Front send ChallengeAccepted
    }

    val challengeDenied =
      (wrapper querySelector "#challenge_denied").asInstanceOf[html.Button]

    challengeDenied.onclick = (me : MouseEvent) => {
      me.preventDefault()
      Front send ChallengeDenied("Denied by user")
    }

  }

  def update(challenger : User,
             rules : Rules) : Unit = {


    val nameWraper = (document getElementById "challengerName").asInstanceOf[html.Span]
    val coreRules = (document getElementById "coreRules").asInstanceOf[html.LI]
    val ostein = (document getElementById "ostein").asInstanceOf[html.LI]
    val allowMulligan = (document getElementById "allowMulligan").asInstanceOf[html.LI]

    nameWraper.textContent = challenger.name
    coreRules.textContent = rules.coreRules.toString

    if(rules.ostein) ostein.show()
    else ostein.hide()

    if(rules.allowMulligan) allowMulligan.show()
    else allowMulligan.hide()


    wrapper.show()
  }
}
