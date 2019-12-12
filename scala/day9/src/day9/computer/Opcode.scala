package day9.computer

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IntMap

sealed abstract class Opcode(val code: Int) extends EnumEntry

object Opcode extends Enum[Opcode] {
  case object Add extends Opcode(1)
  case object Mult extends Opcode(2)
  case object In extends Opcode(3)
  case object Out extends Opcode(4)
  case object JmpIf extends Opcode(5)
  case object JmpIfNot extends Opcode(6)
  case object Lt extends Opcode(7)
  case object Eq extends Opcode(8)
  case object RelBseOffs extends Opcode(9)
  case object End extends Opcode(99)

  private val valuesByCode = IntMap.from(values.map(v => v.code -> v))

  override def values: IndexedSeq[Opcode] = findValues

  def parse(code: Int): Either[Err, Opcode] =
    valuesByCode.get(code).toRight(s"Illegal opcode $code")
}
