package com.rios.marsone.model

case class Rover(cardinalDirection: CardinalDirection, x: Int, y: Int) {

  def left: Rover = this.copy(cardinalDirection = cardinalDirection.left)

  def right: Rover = this.copy(cardinalDirection = cardinalDirection.right)

  def move: Rover = cardinalDirection match {
    case North => this.copy(y = y + 1)
    case South => this.copy(y = y - 1)
    case West => this.copy(x = x - 1)
    case East => this.copy(x = x + 1)
  }
}
