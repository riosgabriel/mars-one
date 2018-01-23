package com.rios.marsone.actors

import akka.actor.{ Actor, Props }

import com.rios.marsone.actors.ControlCenterActor.{ DeployRover, GetRovers }
import com.rios.marsone.model.{ Rover, Rovers }

object ControlCenterActor {

  case object GetRovers
  case class DeployRover(rover: Rover)

  def props: Props = Props[ControlCenterActor]
}

class ControlCenterActor extends Actor {

  var rovers = Set.empty[Rover]

  // improve response
  override def receive: Receive = {
    case DeployRover(rover) =>
      val senderRef = sender()
      rovers += rover
      senderRef ! "Ok"

    case GetRovers =>
      val senderRef = sender()
      senderRef ! Rovers(rovers)
  }
}
