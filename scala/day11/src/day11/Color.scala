package day11

sealed abstract class Color(val code: Long)

object Color {
  case object Black extends Color(0L)
  case object White extends Color(1L)

  def fromCode(code: Long): Either[String, Color] = code match {
    case 0L => Right(Black)
    case 1L => Right(White)
    case _  => Left(s"Illegal color code $code")
  }
}
