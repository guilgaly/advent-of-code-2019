package day11

case class Coord(x: Int, y: Int) {
  def move(orientation: Orientation): Coord = orientation match {
    case Orientation.FaceUp    => copy(y = y + 1)
    case Orientation.FaceRight => copy(x = x + 1)
    case Orientation.FaceDown  => copy(y = y - 1)
    case Orientation.FaceLeft  => copy(x = x - 1)
  }
}
