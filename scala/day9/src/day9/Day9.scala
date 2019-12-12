package day9

import day9.computer.Computer

import scala.io.{Source, StdIn}

object Day9 {

  def main(args: Array[String]): Unit = {
    println("Press enter...")
    StdIn.readLine()

    val rawInput = loadInputFile()
    val parsedProgram = parseInput(rawInput)

    Computer.executeProgram(parsedProgram, inputs = List(1)) match {
      case Left(err) =>
        println(s"[Part 1] Program crashed with error message: $err")
      case Right(res) =>
        println(s"[Part 1] Program terminated with outputs: ${res.outputs}")
    }

    val start = System.nanoTime()
    Computer.executeProgram(parsedProgram, inputs = List(2)) match {
      case Left(err) =>
        println(s"[Part 2] Program crashed with error message: $err")
      case Right(res) =>
        println(s"[Part 2] Program terminated with outputs: ${res.outputs}")
    }
    val end = System.nanoTime()
    val elapsed = (end - start) / (1000 * 1000)
    println(s"elapsed: $elapsed")


    println("Press enter...")
    StdIn.readLine()
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
