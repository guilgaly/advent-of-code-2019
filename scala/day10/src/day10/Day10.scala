package day10

import scala.io.{Source, StdIn}

object Day10 {
  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines().toVector
  }
}
