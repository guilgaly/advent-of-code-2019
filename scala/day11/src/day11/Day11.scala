package day11

import intcode.computer.{Memory, ProgramStatus}

import scala.io.Source

object Day11 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    part1(parsedProgram)

    part2(parsedProgram)
  }

  private def part1(program: Memory): Unit = {
    resolveState(State.init(program, Color.Black)) match {
      case Left(err) =>
        println(s"[Part 1] Program crashed with error message: $err")
      case Right(state) =>
        val painted = state.hull.numberPainted
        println(s"[Part 1] Program terminated with $painted squares painted")
    }
  }

  private def part2(program: Memory): Unit = {
    resolveState(State.init(program, Color.White)) match {
      case Left(err) =>
        println(s"[Part 2] Program crashed with error message: $err")
      case Right(state) =>
        val painted = state.hull.numberPainted
        println(s"[Part 2] Program terminated with $painted squares painted")
        state.hull.toAsciiArt.foreach(println)
    }
  }

  @scala.annotation.tailrec
  private def resolveState(state: State): Either[String, State] = {
    state.next match {
      case Left(err) =>
        Left(err)
      case Right(updatedState)
          if updatedState.programState.status == ProgramStatus.Halted =>
        Right(updatedState)
      case Right(updatedState) =>
        resolveState(updatedState)
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
      if (trimmed.nonEmpty) Some(trimmed.toLong) else None
    }
}
