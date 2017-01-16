package leval.core

import leval.{GameInit, OSteinGameInit, RegularGameInit}

/**
  * Created by lorilan on 9/2/16.
  */

object GameInit {

  def apply(players : Seq[User], rule : Rules) : (Seq[GameInit], Game) =
    players match {
    case p1 +: p2 +: Nil => this.apply(p1, p2, rule)
    case _ => leval.error("two players only")
  }

  def apply(pid1 : User, pid2 : User, rules : Rules) : (Seq[GameInit], Game) =
    if(rules.ostein) {
      val (gi, g) = ostein(pid1, pid2, rules)
      (Seq(gi, gi), g)
    }
    else if (rules.allowMulligan)
      regular(pid1, pid2, rules)
    else
      gameWithoutMulligan(pid1, pid2, rules)

  def ostein(pid1 : User, pid2 : User, rules : Rules) : (OSteinGameInit, Game) = {
    import rules.coreRules.{startingMajesty => majesty}

    val (Seq(hand1, hand2), d3) = hands

    val stars =
      Seq(Star(pid1, majesty),
        Star(pid2, majesty))
    val gi = OSteinGameInit(Seq(pid1, pid2), Seq(hand1, hand2), rules)

    (gi, Game(rules.coreRules, stars, d3))
  }

  def hands : (Seq[Seq[Card]],  Deck) = {
    val deck = deck54()
    val (d2, hand1) = deck pick 9
    val (d3, hand2) = d2 pick 9
    (Seq(hand1, hand2), d3)
  }

  def gameWithoutMulligan(pid1 : User, pid2 : User, rules: Rules) : (Seq[RegularGameInit], Game) = {
    val res @ (_, g) = regular(pid1, pid2, rules)
    if(mulligan(g)) gameWithoutMulligan(pid1, pid2, rules)
    else res
  }

  def regular(pid1 : User, pid2 : User, rules : Rules) : (Seq[RegularGameInit], Game) = {
    val (Seq(h1, h2), d3) = hands
    val (d4, t) = doTwilight(d3)
    val Twilight(Seq(t1, t2)) = t

    val hand1 = h1 ++ t1
    val hand2 = h2 ++ t2

    import rules.coreRules.{startingMajesty => majesty}
    val stars =
      Seq(Star(pid1, majesty, hand1),
        Star(pid2, majesty, hand2))

    val players = Seq(pid1, pid2)
    val gi1 = RegularGameInit(t, players, hand1, rules)
    val gi2 = RegularGameInit(t, players, hand2, rules)
    val firstPlayer =
      if(Card.value(t1.head) > Card.value(t2.head)) 0
      else 1

    (Seq(gi1, gi2),
      Game(rules.coreRules, stars, d4, firstPlayer))
  }

  def mulligan(g : Game) : Boolean =
    g.stars.exists (s => ! hasFace(s.hand))

  def hasFace(h : Set[Card]) =
    h.exists {
      case Joker(_) => true
      case Card(King|Queen|Jack, _) => true
      case _ => false
    }

  def doTwilight(source : Seq[Card]) : (Seq[Card], Twilight) = {
    var d = source
    var h1 = Seq(d.head)
    d = d.tail
    var h2 = Seq(d.head)
    d = d.tail

    while(Card.value(h1.head) == Card.value(h2.head)) d match {
      case c1 +: c2 +: remainings =>
        d = remainings
        h1 = c1 +: h1
        h2 = c2 +: h2

      case Nil | Seq(_) => leval.error()
    }
    (d, Twilight(Seq(h1, h2)))
  }

}