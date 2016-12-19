package leval.controllers

import javax.inject._

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import leval.actors.{LoginActor, UserActor}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, WebSocket}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()
(@Named("loginActor") loginActor : ActorRef)
(implicit actorSystem: ActorSystem,
 materializer: Materializer)
  extends Controller {

  def index = Action {
    Ok(leval.views.html.index())
  }

  val loginForm = Form(
    mapping ("login" -> nonEmptyText)(x => Some(x))(identity)
  )

  def postIndex =
    Action(parse.form(loginForm,
      onErrors = (formErrors: Form[Some[String]]) => Ok(leval.views.html.index()))) {
      implicit request =>
        val Some(login) = request.body
        //loginActor ! LoginActor.Join(login)
        Ok( leval.views.html.starList(login) )
    }


  def ws : WebSocket =
    WebSocket.accept[String, String] { implicit request =>
      ActorFlow.actorRef[String, String]( out => UserActor(loginActor, out))
    }

}
