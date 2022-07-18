package com.sap1ens.api

import akka.actor.{ActorLogging, ActorRef, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import spray.json.DefaultJsonProtocol
import com.sap1ens.utils.ConfigHolder
import com.sap1ens.{Core, CoreActors, Services}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}

trait CORSSupport extends Directives {
  private val CORSHeaders = List(
    `Access-Control-Allow-Methods`(GET, POST, PUT, DELETE, OPTIONS),
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent"),
    `Access-Control-Allow-Credentials`(true)
  )

  def respondWithCORS(origin: String)(routes: => Route) = {
    val originHeader = `Access-Control-Allow-Origin`.*

    respondWithHeaders(originHeader :: CORSHeaders) {
      routes ~ options { complete(StatusCodes.OK) }
    }
  }
}

trait Api extends Directives with CORSSupport with ConfigHolder {
  this: CoreActors with Core =>

  val routes =
    respondWithCORS(config.getString("origin.domain")) {
      pathPrefix("api") {
        new Example1Routes(services).route ~
        new Example2Routes().route
      }
    }

  Http().newServerAt(config.getString("hostname"), port = config.getInt("port")).bind(routes)
}

object ApiRoute {
  case class Message(message: String)

  object ApiRouteProtocol extends DefaultJsonProtocol {
    implicit val messageFormat = jsonFormat1(Message)
  }

  object ApiMessages {
    val UnknownException = "Unknown exception"
    val UnsupportedService = "Sorry, provided service is not supported."
  }
}

abstract class ApiRoute(services: Services = Services.empty) extends Directives with SprayJsonSupport {

  import com.sap1ens.api.ApiRoute.{ApiMessages, Message}
  import com.sap1ens.api.ApiRoute.ApiRouteProtocol._

  def withService(id: String)(action: ActorRef => Route) = {
    services.get(id.toLowerCase) match {
      case Some(provider) =>
        action(provider)

      case None =>
//        log.error(s"Unsupported service: $id")
        complete(StatusCodes.BadRequest, Message(ApiMessages.UnsupportedService))
    }
  }
}
