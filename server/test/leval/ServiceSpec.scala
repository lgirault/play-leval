package leval

import akka.stream.Materializer
import leval.controllers.HomeController._
import org.scalatest.{EitherValues, FeatureSpec, Matchers}
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

/**
  * Created by lorilan on 12/24/16.
  */
class ServiceSpec extends FeatureSpec
  with Matchers
  with EitherValues
  with Results
  with BodyParsers
  with OneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer

  feature("parse body"){

    scenario("EssentialAction example"){
      val action: EssentialAction = Action { request =>
        val value = (request.body.asJson.get \ "field").as[String]
        Ok(value)
      }

      val request = FakeRequest(POST, "/").withJsonBody(Json.parse("""{ "field": "value" }"""))

      val result = call(action, request)

      status(result) shouldBe OK
      contentAsString(result) shouldBe "value"

    }

    scenario("login parse"){
      val action: EssentialAction =
        Action(parse.form(loginForm)){ request =>
        Ok(request.body)
      }

      val formData = Map("login"-> Seq("toto"))

      val request = FakeRequest(POST, "/").copyFakeRequest(body = formData)

      val result = call(action, request)

      status(result) shouldBe OK
      contentAsString(result) shouldBe "toto"

    }
  }
}