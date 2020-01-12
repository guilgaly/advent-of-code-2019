package day13

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.LongMap

sealed abstract class Tile(val code: Long) extends EnumEntry

object Tile extends Enum[Tile] {
  case object Empty extends Tile(0L)
  case object Wall extends Tile(1L)
  case object Block extends Tile(2L)
  case object Paddle extends Tile(3L)
  case object Ball extends Tile(4L)

  private val valuesByCode = LongMap.from(values.map(v => v.code -> v))

  override def values: IndexedSeq[Tile] = findValues

  def parse(code: Long): Either[String, Tile] =
    valuesByCode.get(code).toRight(s"Illegal tile code $code")
}
