package leval.gameScreen

import com.thoughtworks.binding.Binding
import leval.Ids
import leval.core.Game.StarIdx
import leval.core.{ActPhase, InfluencePhase, Phase, SourcePhase}
import org.scalajs.dom.document
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  * Created by lorilan on 1/12/17.
  */
object LeftColumn {

  def phaseTxt(p : Phase) : String = {
    val id = p match {
      case InfluencePhase(_) => Ids.influencePhase
      case ActPhase(_) => Ids.actPhase
      case SourcePhase => Ids.sourcePhase
    }
    (document getElementById s"messages_$id").textContent
  }

  def context(pg : PartialGame) : Binding[Div] =
    Binding {
      div(`class` := "textBox",
        span(`class` := "messages", attr("msgId") := Ids.round),
        pg.currentRound.bind.toString, br,
        pg.stars(pg.currentStarIdx.bind).player.name, br,
        phaseTxt(pg.currentPhase.bind)
      ).render
    }

  def starInfos(pg : PartialGame, idx : StarIdx) : Binding[Div] =
    Binding {
      div(`class` := "textBox",
        pg.stars(idx).player.name, br,
        span(`class` := "messages", attr("msgId") := Ids.majesty ), ":", br,
        pg.stars(idx).majesty.bind.toString
      ).render
    }
}
