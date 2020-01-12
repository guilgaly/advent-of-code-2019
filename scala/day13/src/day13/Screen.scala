package day13

import scala.collection.immutable.HashMap

case class Screen(tiles: Map[Coord, Tile]) {
  def getTile(coord: Coord): Tile =
    tiles.getOrElse(coord, Tile.Empty)

  def drawTile(coord: Coord, tile: Tile): Screen =
    copy(tiles = tiles + (coord -> tile))

  def countTiles(tile: Tile): Int =
    tiles.values.count(_ == tile)
}

object Screen {
  def init: Screen = Screen(HashMap.empty)
}
