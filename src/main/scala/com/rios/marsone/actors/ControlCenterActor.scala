package com.rios.marsone.actors

import akka.actor.{ Actor, Props }
import akka.io.Tcp.Message
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.model.{ Plateau, Rover }

final case class Rovers(rovers: Set[Rover])

object ControlCenterActor {

  case object GetRovers
  case object GetPlateau
  case class DeployRover(rover: Rover)
  case class SetPlateau(plateau: Plateau)

  // improve the name of responses
  case class OkResponse(message: String)
  case class ErrorResponse(message: String)

  def props: Props = Props[ControlCenterActor]
}

class ControlCenterActor extends Actor {

  var rovers = Set.empty[Rover]
  var plateau: Option[Plateau] = None

  // improve response
  override def receive: Receive = {
    case SetPlateau(newPlateau) =>
      val senderRef = sender()

      if (plateau.isEmpty) {
        this.plateau = Some(newPlateau)
        senderRef ! OkResponse(s"Plateau was set")

      } else {
        senderRef ! ErrorResponse(s"Plateau is already set")
      }

    case DeployRover(rover) =>
      val senderRef = sender()

      if (plateau.isDefined) {
        rovers += rover
        senderRef ! OkResponse("Rover was deployed")
      } else {
        senderRef ! ErrorResponse("Could not deploy Rover: Plateau is not set")
      }

    case GetRovers => sender() ! Rovers(rovers)

    case GetPlateau => sender() ! plateau
  }
}
