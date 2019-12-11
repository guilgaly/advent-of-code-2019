package day7

import cats.implicits._
import day7.computer.Value

import scala.io.Source

object Day7 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    val allPhaseSettings: List[List[Value]] =
      List(0, 1, 2, 3, 4).permutations.toList

    val allResults: Either[String, List[Value]] =
      allPhaseSettings
        .traverse { phaseSettings =>
          Amplifier
            .thrusterSignal(parsedProgram, phaseSettings)
        }

    val bestResult = allResults.map(_.max)

    bestResult match {
      case Left(err) =>
        println(s"[Part 1] Program crashed with error message: $err")
      case Right(res) =>
        println(s"[Part 1] Best result: $res")
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
}
