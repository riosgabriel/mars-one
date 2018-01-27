package com.rios.marsone.actors

import akka.actor.{ Actor, Props }
import akka.io.Tcp.Message
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.model.{ Plateau, Rover }

final case class Rovers(rovers: Set[Rover])

object ControlCenterActor {

  case object GetRovers
  case class DeployRover(rover: Rover)
  case class SetPlateau(plateau: Plateau)

  // improve the name of responses
  case class OkResponse(message: String)
  case class NokResponse(message: String)

  def props: Props = Props[ControlCenterActor]
}

class ControlCenterActor extends Actor {

  var rovers = Set.empty[Rover]
  var plateau: Option[Plateau] = None

  // improve response
  override def receive: Receive = {
    case SetPlateau(newPlateau) =>
      this.plateau = Some(newPlateau)
      val senderRef = sender()
      senderRef ! OkResponse(s"Plateau was set")

    case DeployRover(rover) =>
      val senderRef = sender()

      if (plateau.isDefined) {
        rovers += rover
        senderRef ! OkResponse("Rover was deployed")
      } else {
        senderRef ! NokResponse("Could not deploy Rover: Plateau is not set")
      }

    case GetRovers =>
      val senderRef = sender()
      senderRef ! Rovers(rovers)
  }
}
