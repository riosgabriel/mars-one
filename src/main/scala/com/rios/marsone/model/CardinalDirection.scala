package com.rios.marsone.model

sealed trait CardinalDirection {
  val left: CardinalDirection
  val right: CardinalDirection
}

case object North extends CardinalDirection {
  override val left: CardinalDirection = West
  override val right: CardinalDirection = East
}

case object South extends CardinalDirection {
  override val left: CardinalDirection = East
  override val right: CardinalDirection = West
}

case object West extends CardinalDirection {
  override val left: CardinalDirection = South
  override val right: CardinalDirection = North
}

case object East extends CardinalDirection {
  override val left: CardinalDirection = North
  override val right: CardinalDirection = South
}