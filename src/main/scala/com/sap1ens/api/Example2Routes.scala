package com.sap1ens.api

import scala.concurrent.ExecutionContext

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class Example2Routes(implicit ec: ExecutionContext) extends ApiRoute {

  val route: Route =
    path("example2") {
      get {
        complete("Done!")
      }
    }
}