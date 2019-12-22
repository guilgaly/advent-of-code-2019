package day10

case class PolarPoint(x: Int, y: Int, t: Double, r: Double)

object PolarPoint {
  def apply(cartesian: CartesianPoint): PolarPoint = {
    val x: Int = cartesian.x
    val y: Int = cartesian.y
    val t: Double = flippedAtan2(y.toDouble, x.toDouble)
    val r: Double = Math.sqrt(((x * x) + (y * y)).toDouble)
    new PolarPoint(x, y, t, r)
  }

  private def flipAngle(angle: Double) = angle - 1.5 * Math.PI

  private def flippedAtan2(y: Double, x: Double) = {
    val angle = Math.atan2(y, x)
    val flippedAngle = flipAngle(angle)
    //  additionally put the angle into [0; 2*Pi) range from its [-pi; +pi] range
    val x1 = if (flippedAngle >= 0) flippedAngle else flippedAngle + 2 * Math.PI
    if (x1 >= 0) x1 else x1 + 2 * Math.PI
  }
}
