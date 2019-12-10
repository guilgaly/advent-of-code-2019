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
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines()
  }
}
