package day8

import scala.io.Source

object Day8 {
  private val width = 25
  private val height = 6
  private val layerSize = width * height

  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val imageData = parseInput(rawInput)

    val layers = imageData.grouped(layerSize).toVector

    part1(layers)
    part2(layers)
  }

  private def part1(layers: Vector[Vector[Int]]): Unit = {
    val layerWithMinZeroes =
      layers.map(layer => (layer, layer.count(_ == 0))).minBy(_._2)._1
    val oneDigitCount = layerWithMinZeroes.count(_ == 1)
    val twoDigitCount = layerWithMinZeroes.count(_ == 2)
    println(s"[part 1] Result: ${oneDigitCount * twoDigitCount}")
  }

  private def part2(layers: Vector[Vector[Int]]): Unit = {
    val initImage = Vector.fill(layerSize)(2) // start with transparent pixels
    val renderedImage = layers.foldLeft(initImage) { (acc, layer) =>
      acc.zip(layer).map {
        case (top, bellow) => if (top == 2) bellow else top
      }
    }
    println()
    renderedImage
      .map(pixel => if (pixel == 0) "\u2588" else " ")
      .grouped(width)
      .map(_.mkString(""))
      .foreach(println)
  }

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream("input")
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines().next()
  }

  private def parseInput(rawInput: String) =
    rawInput.toVector.map(toInt)

  def toInt(char: Char) = char match {
    case '0' => 0
    case '1' => 1
    case '2' => 2
    case '3' => 3
    case '4' => 4
    case '5' => 5
    case '6' => 6
    case '7' => 7
    case '8' => 8
    case '9' => 9
    case _   => throw new Exception(s"not a digit: $char")
  }
}
