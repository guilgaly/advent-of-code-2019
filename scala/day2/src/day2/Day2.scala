package day2

import scala.io.Source

object Day2 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    part1(parsedProgram)

    part2(parsedProgram)
  }

  private def part1(parsedProgram: Memory): Unit = {
    val resumedProgram = initProgram(parsedProgram, 12, 2)
    Computer
      .executeProgram(resumedProgram)
      .fold(
        err =>
          println(
            s"[part 1] Program crashed with error message: ${err.message}",
          ),
        res =>
          println(
            s"[part 1] Program completed successfully with end state: ${res.mkString(",")}",
          ),
      )
  }

  private def part2(parsedProgram: Memory): Unit = {
    def findNounAndVerb = {
      val possibleValues =
        for {
          noun <- 0 to 99
          verb <- 0 to 99
        } yield (noun, verb)

      possibleValues.find {
        case (noun, verb) =>
          val prog = initProgram(parsedProgram, noun, verb)
          Computer.executeProgram(prog).exists(_(0) == 19690720)
      }
    }

    findNounAndVerb match {
      case None =>
        println("[part 2] Unable to find a valid noun and verb")
      case Some((noun, verb)) =>
        println(s"[part 2] Found noun: $noun, verb: $verb")
        val result = 100 * noun + verb
        println(s"[part 2] Final result: $result")
    }
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

  private def initProgram(program: Memory, noun: Int, verb: Int) =
    program.updated(1, noun).updated(2, verb)
}
