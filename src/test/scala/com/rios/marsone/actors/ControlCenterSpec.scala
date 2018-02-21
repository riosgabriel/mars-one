package com.rios.marsone.actors

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import com.rios.marsone.actors.ControlCenterActor._
import com.rios.marsone.model.{ North, Plateau, Rover }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.ExecutionContext.Implicits.global

class ControlCenterSpec extends TestKit(ActorSystem("RoverActorSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The Control Center actor" must {

    "return none if Plateau is not set " in {
      val roverActor = system.actorOf(ControlCenterActor.props)

      roverActor ! GetPlateau

      expectMsg(None)
    }

    "return a Plateau if it was set" in {
      val plateau = Plateau(1, 1)

      val roverActor = system.actorOf(ControlCenterActor.props)

      roverActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      roverActor ! GetPlateau

      expectMsg(Some(plateau))
    }

    "return the state of the rover22" in {
      val plateau = Plateau(1, 1)

      val roverActor = system.actorOf(ControlCenterActor.props)
      roverActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))
    }

    "return the state of the rover32" in {
      val plateau = Plateau(1, 1)

      val roverActor = system.actorOf(ControlCenterActor.props)
      roverActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      val newPlateau = Plateau(2, 2)

      roverActor ! SetPlateau(newPlateau)

      expectMsg(PlateauAlreadySet("Plateau is already set"))
    }

    "Deploy Rover Test1" in {
      val plateau = Plateau(5, 5)
      val rover = Rover(1, North, 0, 0)

      val roverActor = system.actorOf(ControlCenterActor.props)

      roverActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      roverActor ! DeployRover(rover)

      expectMsg(RoverDeployed("Rover was deployed"))
    }

    "Deploy Rover Test2" in {
      val plateau = Plateau(5, 5)
      val rover = Rover(1, North, 0, 0)

      val roverActor = system.actorOf(ControlCenterActor.props)

      roverActor ! SetPlateau(plateau)

      expectMsg(PlateauSet("Plateau was set"))

      roverActor ! DeployRover(rover)

      expectMsg(RoverDeployed("Rover was deployed"))

      roverActor ! DeployRover(rover)

      expectMsg(DuplicatedRover(s"Could not deploy Rover: Rover with id=${rover.id} has already been deployed"))
    }

    "Deploy Rover Test3" in {
      val rover = Rover(1, North, 0, 0)

      val roverActor = system.actorOf(ControlCenterActor.props)

      roverActor ! DeployRover(rover)

      expectMsg(PlateauNotSet("Could not deploy Rover: Plateau is not set"))
    }
  }
}
