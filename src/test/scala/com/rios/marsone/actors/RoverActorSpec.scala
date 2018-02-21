package com.rios.marsone.actors

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit, TestProbe }
import com.rios.marsone.actors.RoverActor.{ GetState, MoveForward, TurnLeft, TurnRight }
import com.rios.marsone.model.{ North, Plateau, Rover }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.Await
import scala.concurrent.duration._

class RoverActorSpec extends TestKit(ActorSystem("RoverActorSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Rover actor" must {

    "return the state of the rover" in {
      val rover = Rover(1, North, 1, 1)

      val roverActor = system.actorOf(RoverActor.props(rover))
      roverActor ! GetState
      expectMsg(rover)
    }

    "move the rover through plateau" in {
      val rover = Rover(1, North, 1, 1)
      val plateau = Plateau(2, 2)

      val roverActorRef = TestActorRef[RoverActor](RoverActor.props(rover))

      roverActorRef ! MoveForward(plateau)

      roverActorRef.underlyingActor.rover shouldEqual rover.move
    }

    "ignore invalid actions" in {
      val rover = Rover(1, North, 1, 1)
      val plateau = Plateau(2, 2)

      val roverActor = system.actorOf(RoverActor.props(rover))

      roverActor ! MoveForward(plateau)
      roverActor ! MoveForward(plateau)
      roverActor ! MoveForward(plateau)

      roverActor ! GetState
      expectMsg(rover.move)
    }

    "turn left the rover" in {
      val rover = Rover(1, North, 1, 1)

      val roverActor = system.actorOf(RoverActor.props(rover))

      roverActor ! TurnLeft

      expectNoMessage(200 millis)

      roverActor ! GetState

      expectMsg(rover.left)
    }

    "turn right the rover" in {
      val rover = Rover(1, North, 1, 1)

      val roverActor = system.actorOf(RoverActor.props(rover))

      roverActor ! TurnRight

      expectNoMessage(200 millis)

      roverActor ! GetState

      expectMsg(rover.right)
    }

  }
}
