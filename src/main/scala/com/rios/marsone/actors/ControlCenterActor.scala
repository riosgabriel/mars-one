package com.rios.marsone.actors

import akka.actor.{ Actor, Props }

import com.rios.marsone.actors.ControlCenterActor.{ DeployRover, GetRovers }
import com.rios.marsone.model.Rover

final case class Rovers(rovers: Set[Rover])

object ControlCenterActor {

  case object GetRovers
  case class DeployRover(rover: Rover)

  def props: Props = Props[ControlCenterActor]
}

class ControlCenterActor extends Actor {

  var rovers = Set.empty[Rover]

  override def receive: Receive = {
    case DeployRover(rover) => rovers += rover

    case GetRovers =>
      val senderRef = sender()
      senderRef ! rovers
  }
}
