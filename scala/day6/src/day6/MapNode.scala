package day6

final case class MapNode(center: String, orbits: List[MapNode]) {
  def allOrbits: List[MapNode] =
    orbits ++ orbits.flatMap(_.allOrbits)

  def totalOrbitsCount: Int = {
    val indirectCount = orbits.map(_.totalOrbitsCount).sum
    allOrbits.size + indirectCount
  }

  def contains(other: String): Boolean =
    outwardDistanceTo(other).isDefined // could be optimized easily...

  def outwardDistanceTo(other: String): Option[Int] = {
    def searchOrbits = orbits.flatMap(_.outwardDistanceTo(other)) match {
      case Nil      => None
      case nonEmpty => Some(1 + nonEmpty.min)
    }

    if (center == other) Some(0) else searchOrbits
  }
}

object MapNode {
  implicit val ordering: Ordering[MapNode] =
    (x: MapNode, y: MapNode) => x.center.compareTo(y.center)
}
