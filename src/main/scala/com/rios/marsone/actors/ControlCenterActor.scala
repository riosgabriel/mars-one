package com.rios.marsone.actors

import akka.actor.{ Actor, ActorRef, Props }
import akka.pattern.ask
import akka.pattern.pipe

import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.actors.RoverActor.{ GetState, MoveAction }
import com.rios.marsone.model.{ Plateau, Rover }
import scala.collection.mutable
import scala.concurrent.Future

import akka.util.Timeout

// improvement needed
final case class Rovers(rovers: List[Rover])

object ControlCenterActor {
  case object GetPlateau
  case object GetRovers

  case class DeployRover(rover: Rover)
  case class SetPlateau(plateau: Plateau)
  case class MoveRover(id: Long)
  case class RotateToLeft(id: Long)
  case class RotateToRight(id: Long)

  sealed trait Action {
    val description: String
  }

  case class ActionPerformed(description: String) extends Action
  case class ActionNotPerformed(description: String) extends Action

  def props: Props = Props[ControlCenterActor]
}

class ControlCenterActor extends Actor {

  // maybe improve this changing Set[ActorRef] to Map[Int, ActorRef]
  val rovers = mutable.Set.empty[ActorRef]
  var plateau: Option[Plateau] = None

  override def receive: Receive = {

    case SetPlateau(newPlateau) =>
      if (plateau.isDefined) {
        sender() ! ActionNotPerformed("Plateau is already set")

      } else {
        this.plateau = Some(newPlateau)
        sender() ! ActionPerformed("Plateau was set")
      }

    case GetPlateau => sender() ! plateau

    case DeployRover(rover) =>
      if (plateau.isDefined) {
        val roverActor = context.actorOf(RoverActor.props(rover), s"roverActor${rover.id}")
        rovers += roverActor
        sender() ! ActionPerformed("Rover was deployed")

      } else {
        sender() ! ActionNotPerformed("Could not deploy Rover: Plateau is not set")
      }

    case MoveRover(id) =>
      findActorByName(id) match {
        case Some(roverActor) =>
          roverActor ! MoveAction
          sender() ! ActionPerformed(s"Rover was moved")

        case None =>
          sender() ! ActionNotPerformed("Could not find Rover")
      }

    case RotateToLeft(id) =>
      findActorByName(id) match {
        case Some(roverActor) =>
          roverActor ! MoveAction
          sender() ! ActionPerformed(s"Rover was rotated to left")

        case None =>
          sender() ! ActionNotPerformed("Could not find Rover")
      }

    case RotateToRight(id) =>
      findActorByName(id) match {
        case Some(roverActor) =>
          roverActor ! MoveAction
          sender() ! ActionPerformed(s"Rover was rotated to right")

        case None =>
          sender() ! ActionNotPerformed("Could not find Rover")
      }

    // fetch all children actor and map to Rovers
    case GetRovers =>
      import scala.concurrent.ExecutionContext.Implicits.global
      import scala.concurrent.duration._
      implicit lazy val timeout: Timeout = Timeout(5 seconds)

      val senderRef = sender()

      // what a mess, hun?
      val listOfFutures: List[Future[Rover]] = rovers.map { actor =>
        (actor ? GetState).mapTo[Rover]
      }.toList

      Future.sequence(listOfFutures).map(x => Rovers(x)).foreach(x => senderRef ! x)
  }

  def findActorByName(id: Long): Option[ActorRef] = rovers.find(_.path.name == s"roverActor$id")
}
