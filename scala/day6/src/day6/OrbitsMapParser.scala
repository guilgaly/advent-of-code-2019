package day6

object OrbitsMapParser {
  def parse(lines: List[(String, String)]): MapNode = {
    def buildNode(name: String): MapNode = {
      val orbits = lines.collect {
        case (`name`, orbiter) => buildNode(orbiter)
      }
      MapNode(name, orbits)
    }
    buildNode("COM")
  }
}
