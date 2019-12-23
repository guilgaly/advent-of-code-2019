package day11

import day11.Direction._

sealed trait Orientation {
  import Orientation._

  def turn(direction: Direction): Orientation = (this, direction) match {
    case (FaceLeft, RightTurn) | (FaceRight, LeftTurn) => FaceUp
    case (FaceUp, RightTurn) | (FaceDown, LeftTurn)    => FaceRight
    case (FaceRight, RightTurn) | (FaceLeft, LeftTurn) => FaceDown
    case (FaceDown, RightTurn) | (FaceUp, LeftTurn)    => FaceLeft
  }
}

object Orientation {
  case object FaceUp extends Orientation
  case object FaceRight extends Orientation
  case object FaceDown extends Orientation
  case object FaceLeft extends Orientation
}
