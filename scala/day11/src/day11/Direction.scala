package day11

sealed trait Direction
object Direction {
  case object LeftTurn extends Direction
  case object RightTurn extends Direction

  def fromCode(code: Long): Either[String, Direction] = code match {
    case 0L => Right(Direction.LeftTurn)
    case 1L => Right(Direction.RightTurn)
    case _  => Left(s"Illegal direction code $code")
  }
}
