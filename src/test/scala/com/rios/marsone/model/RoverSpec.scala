package com.rios.marsone.model

import org.scalatest.{FlatSpec, Matchers}

class RoverSpec extends FlatSpec with Matchers {

  val roverTest = Rover(id = 1, North, 1, 1)

  "Rover" should "move" in {
    val rover = roverTest.move

    rover.y shouldEqual 2
    rover.x shouldEqual 1
    rover.cardinalDirection shouldEqual North
  }

  it should "rotate to West" in {
    val rover = roverTest.left

    rover.y shouldEqual 1
    rover.x shouldEqual 1
    rover.cardinalDirection shouldEqual West
  }

  it should "rotate to North" in {
    val rover = roverTest.left.left.left.left

    rover.y shouldEqual 1
    rover.x shouldEqual 1
    rover.cardinalDirection shouldEqual North
  }

  it should "rotate to East and move 2x" in {
    val rover = roverTest.right.move.move

    rover.y shouldEqual 1
    rover.x shouldEqual 3
    rover.cardinalDirection shouldEqual East
  }
}
