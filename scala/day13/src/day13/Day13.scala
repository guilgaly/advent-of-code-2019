package day13

import scala.io.Source

object Day13 {

  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    part1(parsedProgram)
  }

  private def part1(program: Vector[Long]): Unit = {
    def recurs(state: Either[String, GameState]): Either[String, GameState] =
      state match {
        case err: Left[_, _]              => err
        case res @ Right(s) if s.finished => res
        case Right(s)                     => recurs(s.next)
      }

    recurs(Right(GameState.init(program))) match {
      case Left(err) =>
        println(s"[Part 1] Program crashed with error message: $err")
      case Right(state) =>
        val blocks = state.screen.countTiles(Tile.Block)
        println(s"[Part 1] Program finished with $blocks blocks")
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
