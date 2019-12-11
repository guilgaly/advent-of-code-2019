package day7

import cats.implicits._
import day7.computer.Value

import scala.io.Source

object Day7 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    part1(parsedProgram)
    part2(parsedProgram)
  }

  private def part1(program: Vector[Value]) = {
    val allPhaseSettings: List[List[Value]] =
      List(0, 1, 2, 3, 4).permutations.toList

    val allResults: Either[String, List[Value]] =
      allPhaseSettings
        .traverse { phaseSettings =>
          DirectAmplifier
            .thrusterSignal(program, phaseSettings)
        }

    val bestResult = allResults.map(_.max)

    bestResult match {
      case Left(err) =>
        println(s"[Part 1] Program crashed with error message: $err")
      case Right(res) =>
        println(s"[Part 1] Best result: $res")
    }
  }

  private def part2(program: Vector[Value]) = {
    val allPhaseSettings: List[List[Value]] =
      List(5, 6, 7, 8, 9).permutations.toList

    val allResults: Either[String, List[Value]] =
      allPhaseSettings
        .traverse { phaseSettings =>
          FeedbackAmplifier
            .thrusterSignal(program, phaseSettings)
        }

    val bestResult = allResults.map(_.max)

    bestResult match {
      case Left(err) =>
        println(s"[Part 2] Program crashed with error message: $err")
      case Right(res) =>
        println(s"[Part 2] Best result: $res")
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
