package com.rios.marsone.model

import org.scalatest.{FlatSpec, Matchers}

class CardinalDirectionSpec extends FlatSpec with Matchers {

  "North" should "have West on it left" in {
    North.left shouldEqual West
  }

  it should "have East on it right" in {
    North.right shouldEqual East
  }

  "South" should "have East on it left" in {
    South.left shouldEqual East
  }

  it should "have West on it right" in {
    South.right shouldEqual West
  }

  "East" should "have North on it left" in {
    East.left shouldEqual North
  }

  it should "have South on it right" in {
    East.right shouldEqual South
  }

  "West" should "have South on it left" in {
    West.left shouldEqual South
  }

  it should "have North on it right" in {
    West.right shouldEqual North
  }
}
