package com.rios.marsone.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.rios.marsone.actors.RoverActor.{GetState, MoveForward, TurnLeft, TurnRight}
import com.rios.marsone.model.{Plateau, Rover}

object RoverActor {

  case object GetState

  case object TurnLeft

  case object TurnRight

  case class MoveForward(boundaries: Plateau)

  def props(rover: Rover): Props = Props(new RoverActor(rover))
}

class RoverActor(var rover: Rover) extends Actor with ActorLogging {

  override def receive: Receive = {
    case GetState => sender() ! rover

    case MoveForward(boundaries) =>
      val newState = rover.move

      if (outOfBoundaries(newState, boundaries)) {
        log.error(s"Invalid movement, outside of boundaries")

      } else {
        rover = newState
      }

    case TurnLeft => rover = rover.left

    case TurnRight => rover = rover.right
  }

  def outOfBoundaries(rover: Rover, boundaries: Plateau): Boolean =
    rover.x < 0 || rover.x > boundaries.x || rover.y < 0 || rover.y > boundaries.y
}
