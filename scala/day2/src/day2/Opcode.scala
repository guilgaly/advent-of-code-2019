package day2

sealed abstract class Opcode(val code: Int)

object Opcode {
  case object Add extends Opcode(1)
  case object Mult extends Opcode(2)
  case object End extends Opcode(99)

  def parse(code: Int): Either[String, Opcode] = code match {
    case 1     => Right(Add)
    case 2     => Right(Mult)
    case 99    => Right(End)
    case other => Left(s"Illegal opcode $other")
  }
}
