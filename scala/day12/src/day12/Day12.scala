package day12

import org.apache.commons.math3.util.ArithmeticUtils

object Day12 {
  def main(args: Array[String]): Unit = {
    val initSystem = List(
      Moon("Io", 17, -9, 4, 0, 0, 0),
      Moon("Europa", 2, 2, -13, 0, 0, 0),
      Moon("Ganymede", -1, 5, -1, 0, 0, 0),
      Moon("Callisto", 4, 7, -7, 0, 0, 0),
    )

    part1(initSystem)

    part2(initSystem)
  }

  private def part1(initSystem: List[Moon]): Unit = {
    def nextStep(system: List[Moon]) =
      system.map(_.applyGravity(system)).map(_.applyVelocity)

    @scala.annotation.tailrec
    def recurs(count: Int, system: List[Moon]): List[Moon] =
      if (count == 1000) system
      else recurs(count + 1, nextStep(system))

    val resultSystem = recurs(0, initSystem)
    val resultEnergy = resultSystem.map(_.energy).sum
    println(s"[Part 1] Final energy: $resultEnergy")
  }

  private def part2(initSystem: List[Moon]): Unit = {
    def nextStep(system: List[SingleDimensionMoon]) =
      system.map(_.applyGravity(system)).map(_.applyVelocity)

    def findPeriod(singleDimensionSystem: List[SingleDimensionMoon]) = {
      @scala.annotation.tailrec
      def recurs(count: Long, system: List[SingleDimensionMoon]): Long =
        if (system == singleDimensionSystem) count
        else recurs(count + 1, nextStep(system))
      recurs(1, nextStep(singleDimensionSystem))
    }

    val xPeriod = findPeriod(initSystem.map { moon =>
      SingleDimensionMoon(moon.name, moon.x, moon.vX)
    })
    println(s"[Part 2] X axis period: $xPeriod")

    val yPeriod = findPeriod(initSystem.map { moon =>
      SingleDimensionMoon(moon.name, moon.y, moon.vY)
    })
    println(s"[Part 2] Y axis period: $yPeriod")

    val zPeriod = findPeriod(initSystem.map { moon =>
      SingleDimensionMoon(moon.name, moon.z, moon.vZ)
    })
    println(s"[Part 2] Z axis period: $zPeriod")

    val period =
      ArithmeticUtils.lcm(ArithmeticUtils.lcm(xPeriod, yPeriod), zPeriod)
    println(s"[Part 2] Period: $period")
  }
}
