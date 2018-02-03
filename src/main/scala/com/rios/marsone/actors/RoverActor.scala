package com.rios.marsone.actors

import akka.actor.{ Actor, ActorLogging, Props }
import com.rios.marsone.actors.RoverActor.{ GetState, LeftAction, MoveAction, RightAction }
import com.rios.marsone.model.Rover

object RoverActor {

  case object GetState
  case object MoveAction
  case object LeftAction
  case object RightAction

  def props(rover: Rover): Props = Props(new RoverActor(rover))
}

class RoverActor(var rover: Rover) extends Actor with ActorLogging {

  override def receive: Receive = {
    case GetState => sender() ! rover

    case MoveAction => rover = rover.move

    case LeftAction => rover = rover.left

    case RightAction => rover = rover.right
  }
}
