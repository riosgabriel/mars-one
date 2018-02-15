package com.rios.marsone.actors

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.ask

import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.actors.RoverActor.{ GetState, MoveForward, TurnLeft, TurnRight }
import com.rios.marsone.model.{ Plateau, Rover }
import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

import akka.util.Timeout

final case class Rovers(rovers: List[Rover])

object ControlCenterActor {

  case object GetPlateau
  case object GetRovers

  case class DeployRover(rover: Rover)
  case class SetPlateau(plateau: Plateau)
  case class Commands(roverId: Int, commands: List[String])

  sealed trait Response {
    def message: String
  }

  case class Ok(message: String) extends Response
  case class NOK(message: String) extends Response

  def props(implicit executionContext: ExecutionContext): Props = Props(new ControlCenterActor(executionContext))
}

class ControlCenterActor(val executionContext: ExecutionContext) extends Actor with ActorLogging {

  private implicit val _executionContext: ExecutionContext = executionContext

  implicit lazy val timeout: Timeout = Timeout(5 seconds)

  val rovers: mutable.Set[ActorRef] = mutable.Set.empty
  var plateau: Option[Plateau] = None

  override def receive: Receive = {

    case SetPlateau(newPlateau) =>
      if (plateau.isDefined) {
        sender() ! NOK("Plateau is already set")

      } else {
        plateau = Some(newPlateau)
        sender() ! Ok("Plateau was set")
      }

    case GetPlateau => sender() ! plateau

    case DeployRover(rover) =>
      if (plateau.isDefined) {
        val roverActor = context.actorOf(RoverActor.props(rover), s"roverActor${rover.id}")
        rovers += roverActor
        sender() ! Ok("Rover was deployed")

      } else {
        sender() ! NOK("Could not deploy Rover: Plateau is not set")
      }

    case Commands(id, commands) =>
      val senderRef = sender()

      findActorByName(id) match {
        case Some(roverActor) =>
          commands.foreach {
            case "M" => roverActor ! MoveForward(plateau.get)
            case "L" => roverActor ! TurnLeft
            case "R" => roverActor ! TurnRight
            case x => log.info(s"Wrong command $x")
          }

          senderRef ! Ok(s"Commands was sent do rover with id=$id")

        case None => senderRef ! NOK(s"Could not find Rover with id=$id")
      }

    case GetRovers =>
      val senderRef = sender()

      val listOfFutures: List[Future[Rover]] = rovers
        .map(actor => (actor ? GetState).mapTo[Rover]).toList

      Future
        .sequence(listOfFutures)
        .map(Rovers)
        .foreach(rovers => senderRef ! rovers)
  }

  def findActorByName(id: Long): Option[ActorRef] = rovers.find(_.path.name == s"roverActor$id")
}
