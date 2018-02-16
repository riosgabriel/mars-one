package com.rios.marsone.actors

import akka.actor.{ Actor, ActorLogging, Props }

import com.rios.marsone.actors.RoverActor.{ GetState, MoveForward, TurnLeft, TurnRight }
import com.rios.marsone.model.{ Plateau, Rover }

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

      if (withinBoundaries(newState, boundaries)) {
        rover = rover.move

      } else {
        log.error(s"Invalid movement, outside of boundaries")
      }

    case TurnLeft => rover = rover.left

    case TurnRight => rover = rover.right
  }

  def withinBoundaries(rover: Rover, boundaries: Plateau): Boolean =
    rover.x <= boundaries.x || rover.y <= boundaries.y
}
