package com.rios.marsone.actors

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.model.{ North, Plateau, Rover, West }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.ExecutionContext.Implicits.global

class ControlCenterSpec extends TestKit(ActorSystem("ControlCenterSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The Control Center actor" must {

    "return none if Plateau was not set " in {
      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! GetPlateau

      expectMsg(None)
    }

    "return a Plateau if it was set" in {
      val plateau = Plateau(1, 1)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      controlCenterActor ! GetPlateau

      expectMsg(Some(plateau))
    }

    "return PlateauSet if Plateau wasn't set previously" in {
      val plateau = Plateau(1, 1)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)
      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))
    }

    "return PlateauAlreadySet if Plateau was set previously" in {
      val plateau = Plateau(1, 1)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)
      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      val newPlateau = Plateau(2, 2)

      controlCenterActor ! SetPlateau(newPlateau)

      expectMsg(PlateauAlreadySet("Plateau is already set"))
    }

    "return RoverDeployed if Plateau was set previously" in {
      val plateau = Plateau(5, 5)
      val rover = Rover(1, North, 0, 0)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      controlCenterActor ! DeployRover(rover)

      expectMsg(RoverDeployed("Rover was deployed"))
    }

    "return DuplicatedRover if trying to deploy a rover with same id" in {
      val plateau = Plateau(5, 5)
      val rover = Rover(1, North, 0, 0)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      controlCenterActor ! DeployRover(rover)

      expectMsg(RoverDeployed("Rover was deployed"))

      controlCenterActor ! DeployRover(rover)

      expectMsg(DuplicatedRover(s"Could not deploy Rover: Rover with id=${rover.id} has already been deployed"))
    }

    "return PlateauNotSet if trying to deploy rover without plateau" in {
      val rover = Rover(1, North, 0, 0)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! DeployRover(rover)

      expectMsg(PlateauNotSet("Could not deploy Rover: Plateau is not set"))
    }

    "return Receive Commands when send commands to a rover" in {
      val plateau = Plateau(5, 5)
      val rover = Rover(1, North, 0, 0)
      val commands = List("M, R, R, M")

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      controlCenterActor ! DeployRover(rover)

      expectMsg(RoverDeployed("Rover was deployed"))

      controlCenterActor ! Commands(rover.id, commands)

      expectMsg(ReceivedCommands(s"Commands was sent do rover with id=${rover.id}"))
    }

    "return empty list of rovers" in {
      val emptyRovers = Rovers(Set.empty)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! GetRovers

      expectMsg(emptyRovers)
    }

    "return a list of rovers" in {
      val plateau = Plateau(5, 5)
      val rover1 = Rover(1, North, 1, 1)
      val rover2 = Rover(2, West, 1, 2)
      val rovers = Set(rover1, rover2)
      val result = Rovers(rovers)

      val controlCenterActor = system.actorOf(ControlCenterActor.props)

      controlCenterActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      controlCenterActor ! DeployRover(rover1)

      expectMsg(RoverDeployed("Rover was deployed"))

      controlCenterActor ! DeployRover(rover2)

      expectMsg(RoverDeployed("Rover was deployed"))

      controlCenterActor ! GetRovers

      expectMsg(result)
    }
  }
}
