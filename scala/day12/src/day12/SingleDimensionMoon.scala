package day12

case class SingleDimensionMoon(name: String, pos: Long, velocity: Long) {

  def applyGravity(moons: Iterable[SingleDimensionMoon]): SingleDimensionMoon =
    moons.foldLeft(this) { (acc, other) =>
      if (other.name == name) acc else acc.applyGravityOf(other)
    }

  def applyVelocity: SingleDimensionMoon =
    copy(pos = pos + velocity)

  private def applyGravityOf(
      other: SingleDimensionMoon,
  ): SingleDimensionMoon = {
    def delta(ownV: Long, otherV: Long) =
      if (ownV < otherV) 1L else if (ownV > otherV) -1L else 0L
    copy(velocity = velocity + delta(pos, other.pos))
  }
}
