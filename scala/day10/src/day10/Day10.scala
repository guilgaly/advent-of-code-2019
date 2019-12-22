package day10

import scala.io.Source

object Day10 {
  private val inputFile = "input"

  implicit private val doubleOrd: Ordering[Double] =
    Ordering.Double.IeeeOrdering

  def main(args: Array[String]): Unit = {
    val rawInput = loadInputFile()
    val asteroids = parse(rawInput).toSet

    val observationLines = asteroids.map { asteroid =>
      calculateObservationLines(asteroid, asteroids - asteroid)
    }
    val (bestObservationPoint, linesCount) = observationLines.maxBy(_._2)
    println(s"[Part 1] Best: $linesCount at point $bestObservationPoint")

    val otherAsteroids = (asteroids - bestObservationPoint).toList
    val translatedCoordinates =
      translateCoordinates(bestObservationPoint, otherAsteroids)
    val polarCoordinates = translatedCoordinates.map(PolarPoint.apply)
    println(s"otherAsteroids: ${otherAsteroids.take(5)}")
    println(s"polarCoordinates: ${polarCoordinates.take(5)}")
    val groupedByAngle = polarCoordinates
      .groupBy(p => Math.round(p.t * 1000))
      .toList
      .sortBy(_._1)
      .map(_._2.sortBy(_.r))
    val orderedAsteroids = orderAsteroids(groupedByAngle).toVector
    val withInitialCoordinates = orderedAsteroids.map { p =>
      CartesianPoint(p.x + bestObservationPoint.x, p.y + bestObservationPoint.y)
    }
//    val withResultCalc = withInitialCoordinates.map(p => p.x * 100 + p.y)
    val withResultCalc = withInitialCoordinates

    println(s"[Part 2]:")
    printnth(withResultCalc, 1)
    printnth(withResultCalc, 2)
    printnth(withResultCalc, 3)
    printnth(withResultCalc, 10)
    printnth(withResultCalc, 20)
    printnth(withResultCalc, 50)
    printnth(withResultCalc, 199)
    printnth(withResultCalc, 200)
    printnth(withResultCalc, 201)
    printnth(withResultCalc, 299)
  }

  private def printnth(vect: Vector[_], n: Int) =
    println(s"$n: ${vect(n - 1)}")

  private def loadInputFile() = {
    val is = getClass.getClassLoader.getResourceAsStream(inputFile)
    val src = Source.fromInputStream(is, "UTF-8")
    src.getLines().toVector
  }

  private def parse(lines: Vector[String]) =
    lines.zipWithIndex.flatMap {
      case (line, y) =>
        line.zipWithIndex.collect { case ('#', x) => CartesianPoint(x, y) }
    }

  private def calculateObservationLines(
      asteroid: CartesianPoint,
      others: Set[CartesianPoint],
  ) = {
    val count = others.toList
      .map { other =>
        // true = there is an obstacle
        CartesianPoint
          .intersectionsBetween(asteroid, other)
          .exists(others.contains)
      }
      .count(!_)
    (asteroid, count)
  }

  private def translateCoordinates(
      center: CartesianPoint,
      asteroids: List[CartesianPoint],
  ) =
    asteroids.map(old => CartesianPoint(old.x - center.x, old.y - center.y))

  private def orderAsteroids(
      groupedByAngle: List[List[PolarPoint]],
  ): List[PolarPoint] = {
    val heads = groupedByAngle.flatMap(_.headOption)
    val tails = groupedByAngle.collect {
      case _ :: tl => tl
    }
    if (tails.isEmpty) heads
    else heads ++ orderAsteroids(tails)
  }
}
