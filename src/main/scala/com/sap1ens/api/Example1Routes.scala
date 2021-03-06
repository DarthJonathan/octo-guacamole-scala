package com.sap1ens.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext
import spray.json.DefaultJsonProtocol
import akka.pattern.ask

import scala.util.{Failure, Success}
import akka.util.Timeout

import scala.concurrent.duration._
import com.sap1ens.ExampleService.ExampleMessage
import com.sap1ens.Services

import scala.language.postfixOps

object Example1Routes {
  case class TestAPIObject(thing: String)

  object Example1RoutesProtocol extends DefaultJsonProtocol {
    implicit val testAPIObjectFormat = jsonFormat1(TestAPIObject)
  }
}

class Example1Routes(services: Services)(implicit ec: ExecutionContext) extends ApiRoute(services) {

  import Example1Routes._
  import Example1RoutesProtocol._
  import com.sap1ens.api.ApiRoute._
  import ApiRouteProtocol._

  implicit val timeout = Timeout(10 seconds)

  val route: Route =
    path("example1" / "test") {
      post {
        entity(as[TestAPIObject]) { request =>
          complete(StatusCodes.OK, s"you send me ${request.thing}")
        }
      }
    } ~
    path("example1" / "done") {
      get {
        complete("Done!")
      }
    } ~
    /**
     * Example of Akka-Spray communication
     *
     * More advanced example: http://techblog.net-a-porter.com/2013/12/ask-tell-and-per-request-actors/
     */
    path("example1" / "service" / Segment / Segment) { (serviceId, command) =>
      get {
        withService(serviceId) { service =>
          val future = (service ? (replyTo => ExampleMessage(command, replyTo))).mapTo[String]

          onComplete(future) {
            case Success(result) =>
              complete(Message(result))

            case Failure(e) =>
//              log.error(s"Error: ${e.toString}")
              complete(StatusCodes.InternalServerError, Message(ApiMessages.UnknownException))
          }
        }
      }
    }
}
