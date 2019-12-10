package day6

final case class MapNode(center: String, orbits: List[MapNode]) {
  def allOrbits: List[MapNode] =
    orbits ++ orbits.flatMap(_.allOrbits)

  def totalOrbitsCount: Int = {
    val indirectCount = orbits.map(_.totalOrbitsCount).sum
    allOrbits.size + indirectCount
  }
}

object MapNode {
  implicit val ordering: Ordering[MapNode] =
    (x: MapNode, y: MapNode) => x.center.compareTo(y.center)
}
