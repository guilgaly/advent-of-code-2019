package day10

import org.apache.commons.math3.util.ArithmeticUtils

case class CartesianPoint(x: Int, y: Int)

object CartesianPoint {
  def intersectionsBetween(
      a: CartesianPoint,
      b: CartesianPoint,
  ): List[CartesianPoint] = {
    val deltaX = b.x - a.x
    val deltaY = b.y - a.y
    val gcd = ArithmeticUtils.gcd(deltaX, deltaY)
    if (gcd < 2) {
      List.empty
    } else {
      val xScale = deltaX / gcd
      val yScale = deltaY / gcd
      var intersects = List.empty[CartesianPoint]
      var i = 1
      var next = CartesianPoint(a.x + i * xScale, a.y + i * yScale)
      while (next != b) {
        intersects = intersects :+ next
        i += 1
        next = CartesianPoint(a.x + i * xScale, a.y + i * yScale)
      }
      intersects
    }
  }
}
