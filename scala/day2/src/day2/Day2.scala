package day2

import scala.io.Source

object Day2 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)
    val resumedProgram = parsedProgram.updated(1, 12).updated(2, 2)

    Computer
      .executeProgram(resumedProgram)
      .fold(
        err => println(s"Program crashed with error message: ${err.message}"),
        res =>
          println(
            s"Program completed successfully with end state: ${res.mkString(",")}",
          ),
      )
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines().next()
  }

  private def parseInput(rawInput: String) =
    rawInput.split(',').toVector.flatMap { elt =>
      val trimmed = elt.trim
      if (trimmed.nonEmpty) Some(trimmed.toInt) else None
    }
}
