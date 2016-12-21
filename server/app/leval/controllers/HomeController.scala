package leval.controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import akka.pattern.ask
import akka.util.Timeout
import leval._
import leval.actors.UserActor
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, WebSocket}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

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

  def index = Action { implicit request =>
    Logger.info("get request received body=" + request.body)
    Ok(leval.views.html.index())
  }

  val loginForm = Form(
    mapping ("login" -> nonEmptyText)(x => Some(x))(identity)
  )

  implicit val timeout = Timeout(500, TimeUnit.MILLISECONDS)

  def postIndex : Action[Some[String]]=
    Action.async(parse.form(loginForm,
      onErrors = (formErrors: Form[Some[String]]) =>
        {Logger.info(formErrors.toString)
        BadRequest("error in form")} )) {
      implicit request =>

        Logger.info("post request received")

        val Some(login) = request.body
        val logRequest =
          (loginActor ? GuestConnect(login)).mapTo[ConnectAnswerMessage]

        logRequest map {
          case ConnectAck(pid) =>
            Ok(leval.views.html.starList(pid.uuid, pid.name))
          case ConnectNack(msg) =>
            BadRequest(msg)
        }
    }


  def ws : WebSocket =
    WebSocket.accept[String, String] { implicit request =>
      ActorFlow.actorRef[String, String]( out => UserActor(loginActor, out))
    }

}
