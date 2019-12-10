package day6

import scala.io.Source

object Day6 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()

    val pattern = """([A-Z0-9]{3})\)([A-Z0-9]{3})""".r
    val inputLines =
      rawInput.collect {
        case pattern(center, orbiter) => (center, orbiter)
      }.toList

    val orbitsMap = OrbitsMapParser.parse(inputLines)

    println(s"All orbits count: ${orbitsMap.totalOrbitsCount}")

    val distanceToSanta =
      for {
        parent <- findClosestParent(orbitsMap)
        dist1 <- parent.outwardDistanceTo("YOU")
        dist2 <- parent.outwardDistanceTo("SAN")
      } yield dist1 + dist2 - 2

    println(s"Distance to Santa: $distanceToSanta")
  }

  private def findClosestParent(nodeToSearch: MapNode): Option[MapNode] = {
    def isParent(node: MapNode) = node.contains("YOU") && node.contains("SAN")

    val search = nodeToSearch.orbits.collectFirst {
      case orbit if isParent(orbit) => findClosestParent(orbit)
    }

    search.fold(
      Some(nodeToSearch).filter(isParent),
    )(
      identity,
    )
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines()
  }
}
