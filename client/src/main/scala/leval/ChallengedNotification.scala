package leval

import org.scalajs.dom._
import leval.core.{PlayerId, Rules}
import Util.DivOps
/**
  * Created by lorilan on 12/25/16.
  */
object ChallengedNotification {

  def update(challenger : PlayerId,
             rules : Rules) : Unit = {

    val elt = (document getElementById "challengedNotification").asInstanceOf[html.Div]

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


    elt.show()
  }
}
