package leval.gameScreen

/**
  * Created by lorilan on 1/11/17.
  */
sealed abstract class MenuMode
case object CreateBeingMenu extends MenuMode
case object PlayCardMenu extends MenuMode
