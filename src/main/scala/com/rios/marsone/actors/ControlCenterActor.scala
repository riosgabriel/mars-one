package com.rios.marsone.actors

import akka.actor.{ Actor, ActorLogging, ActorRef, PoisonPill, Props }
import akka.pattern.ask
import akka.util.Timeout
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.actors.RoverActor.{ GetState, MoveForward, TurnLeft, TurnRight }
import com.rios.marsone.model.{ Plateau, Rover }

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps

final case class Rovers(rovers: Set[Rover])

object ControlCenterActor {

  case object GetPlateau
  case object GetRovers
  case object AbortMission

  case class DeployRover(rover: Rover)
  case class SetPlateau(plateau: Plateau)
  case class Commands(roverId: Long, commands: List[String])

  sealed trait ControlCenterResponse {
    def message: String
  }

  case class RoverDeployed(message: String) extends ControlCenterResponse
  case class DuplicatedRover(message: String) extends ControlCenterResponse
  case class RoverNotFound(message: String) extends ControlCenterResponse
  case class ReceivedCommands(message: String) extends ControlCenterResponse

  case class PlateauSet(message: String) extends ControlCenterResponse
  case class PlateauNotSet(message: String) extends ControlCenterResponse
  case class PlateauAlreadySet(message: String) extends ControlCenterResponse
  case class MissionAborted(message: String) extends ControlCenterResponse

  def props(implicit executionContext: ExecutionContext): Props = Props(new ControlCenterActor(executionContext))
}

class ControlCenterActor(val executionContext: ExecutionContext) extends Actor with ActorLogging {

  private implicit val _executionContext: ExecutionContext = executionContext

  implicit lazy val timeout: Timeout = Timeout(5 seconds)

  val rovers: mutable.Set[ActorRef] = mutable.Set.empty

  var plateau: Option[Plateau] = None

  override def receive: Receive = {

    case GetPlateau => sender() ! plateau

    case SetPlateau(newPlateau) =>
      if (plateau.isDefined) {
        sender() ! PlateauAlreadySet("Plateau is already set")

      } else {
        plateau = Some(newPlateau)
        sender() ! PlateauSet("Plateau was set")
      }

    case DeployRover(rover) =>
      if (plateau.isDefined) {
        findActorByName(rover.id) match {
          case Some(_) =>
            sender() ! DuplicatedRover(s"Could not deploy Rover: Rover with id=${rover.id} has already been deployed")

          case None =>
            val roverActor = context.actorOf(RoverActor.props(rover), s"roverActor${rover.id}")
            rovers += roverActor
            sender() ! RoverDeployed("Rover was deployed")
        }

      } else {
        sender() ! PlateauNotSet("Could not deploy Rover: Plateau is not set")
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

          senderRef ! ReceivedCommands(s"Commands was sent do rover with id=$id")

        case None => senderRef ! RoverNotFound(s"Could not find Rover with id=$id")
      }

    case GetRovers =>
      val senderRef = sender()

      val listOfFutures: Set[Future[Rover]] = rovers
        .map(actor => (actor ? GetState).mapTo[Rover]).toSet

      Future
        .sequence(listOfFutures)
        .map(Rovers)
        .foreach(rovers => senderRef ! rovers)

    case AbortMission =>
      val senderRef = sender()

      abortMission()

      senderRef ! MissionAborted(s"Mission aborted")
  }

  def findActorByName(id: Long): Option[ActorRef] = rovers.find(_.path.name == s"roverActor$id")

  def abortMission(): Unit = {
    rovers.foreach(roverActor => roverActor ! PoisonPill)
    rovers.clear()
    plateau = None
  }
}
