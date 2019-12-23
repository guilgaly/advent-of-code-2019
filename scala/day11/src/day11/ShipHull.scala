package day11

import scala.collection.immutable.HashMap

class ShipHull(painted: Map[Coord, Color]) {
  def paint(coord: Coord, color: Color): ShipHull =
    new ShipHull(painted + (coord -> color))

  def getColor(coord: Coord): Color =
    painted.getOrElse(coord, Color.Black)

  def numberPainted: Int =
    painted.size

  override def toString: String = painted.toString

  def toAsciiArt: List[String] = {
    val xCoords = painted.keys.map(_.x).toVector
    val (minX, maxX) = (xCoords.min, xCoords.max)
    val yCoords = painted.keys.map(_.y).toVector
    val (minY, maxY) = (yCoords.min, yCoords.max)

    (minY to maxY)
      .map { y =>
        (minX to maxX).map { x =>
          getColor(Coord(x, y)) match {
            case Color.Black => " "
            case Color.White => "\u2588"
          }
        }.mkString
      }
      .reverse
      .toList
  }
}

object ShipHull {
  def init = new ShipHull(HashMap.empty)
}
