package day1

import scala.io.Source

object Day1 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val moduleMasses = parseInput(rawInput)

    val fuel1 = Part1.totalFuel(moduleMasses)
    println(s"Ammount of fuel required (part 1): $fuel1")

    val fuel2 = Part2.totalFuel(moduleMasses)
    println(s"Ammount of fuel required (part 2): $fuel2")
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines().toList
  }

  def parseInput(rawInput: List[String]) =
    rawInput.flatMap { line =>
      val trimmed = line.trim
      if (trimmed.nonEmpty) List(trimmed.toLong) else List.empty
    }
}
