package day7.computer

sealed trait Param

object Param {
  final case class Position(address: Address) extends Param
  final case class Immediate(value: Value) extends Param

  def parse(value: Int, mode: Int): Either[Err, Param] =
    mode match {
      case 0     => Right(Position(value))
      case 1     => Right(Immediate(value))
      case other => Left(s"Illegal param mode code $other")
    }
}
