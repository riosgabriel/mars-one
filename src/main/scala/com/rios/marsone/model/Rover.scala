package com.rios.marsone.model

case class Rover(cardinalDirection: String, x: Int, y: Int)

final case class Rovers(rovers: Set[Rover])
