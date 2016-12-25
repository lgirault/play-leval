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
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.ExecutionContext.Implicits.global

object HomeController {
  val loginForm = Form(
    mapping ("login" -> nonEmptyText)(identity)(x => Some(x))
  )


}

import HomeController._

@Singleton
class HomeController @Inject()
(@Named("loginActor") loginActor : ActorRef,
 val messagesApi: MessagesApi)
(implicit actorSystem: ActorSystem,
 materializer: Materializer)
  extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(leval.views.html.index())
   }



  implicit val timeout = Timeout(500, TimeUnit.MILLISECONDS)

  def connect : Action[String]=
    Action.async(parse.form(loginForm,
      onErrors = (formErrors: Form[String]) => BadRequest(s"error : $formErrors") )) {
      implicit request =>

        val login = request.body
        val logRequest =
          (loginActor ? GuestConnect(login)).mapTo[ConnectAnswerMessage]

        logRequest map {
          case ConnectAck(pid) =>
            Ok(leval.views.html.starList(pid))
          case ConnectNack(msg) =>
            BadRequest(msg)
        }
    }

  def challenge = Action(parse.form(ChallengeForm.instance,
    onErrors = (formErrors: Form[IdedMessage]) => BadRequest(s"error : $formErrors") )) { implicit request =>

    loginActor ! request.body

    Ok
  }


  def ws : WebSocket =
    WebSocket.accept[String, String] { implicit request =>
      ActorFlow.actorRef[String, String]( out => UserActor(loginActor, out))
    }

}
