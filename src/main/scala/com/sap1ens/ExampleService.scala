package com.sap1ens

import akka.actor.typed.ActorRef
import akka.actor.{Actor, ActorLogging, Props}

object ExampleService {
  case class ExampleMessage(message: String, client: ActorRef[ExampleResponse])

  case class ExampleResponse(resMessage: String)

  def props(property: String) = Props(classOf[ExampleService], property)
}

class ExampleService(property: String) extends Actor with ActorLogging {
  import ExampleService._

  def receive = {
    case ExampleMessage(message, replyTo) => {
      log.info(s"Example $message with property $property!")

      replyTo ! ExampleResponse(resMessage = s"Example $message with property $property!")
    }
  }
}
