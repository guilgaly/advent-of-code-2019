package intcode.computer

sealed trait Param

object Param {
  sealed trait ReadWrite extends Param

  final case class Position(address: Address) extends ReadWrite
  final case class Immediate(value: Value) extends Param
  final case class Relative(shift: Int) extends ReadWrite

  def parse(value: Long, mode: Int): Either[Err, Param] =
    mode match {
      case 0     => valueToAddress(value).map(Position.apply)
      case 1     => Right(Immediate(value))
      case 2     => Right(Relative(value.toInt))
      case other => Left(s"Illegal param mode code $other")
    }

  def parseReadWrite(value: Long, mode: Int): Either[Err, Param.ReadWrite] =
    mode match {
      case 0     => valueToAddress(value).map(Position.apply)
      case 2     => Right(Relative(value.toInt))
      case other => Left(s"Illegal param mode code $other")
    }
}
