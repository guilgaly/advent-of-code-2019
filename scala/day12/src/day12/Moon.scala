package day12

case class Moon(
    name: String,
    x: Long,
    y: Long,
    z: Long,
    vX: Long,
    vY: Long,
    vZ: Long,
) {
  def energy: Long = {
    val potentialEnergy = Math.abs(x) + Math.abs(y) + Math.abs(z)
    val kineticEnergy = Math.abs(vX) + Math.abs(vY) + Math.abs(vZ)
    potentialEnergy * kineticEnergy
  }

  def applyGravity(moons: Iterable[Moon]): Moon =
    moons.foldLeft(this) { (acc, other) =>
      if (other.name == name) acc else acc.applyGravityOf(other)
    }

  def applyVelocity: Moon =
    copy(x = x + vX, y = y + vY, z = z + vZ)

  private def applyGravityOf(other: Moon): Moon = {
    def delta(ownV: Long, otherV: Long) =
      if (ownV < otherV) 1L else if (ownV > otherV) -1L else 0L

    copy(
      vX = vX + delta(x, other.x),
      vY = vY + delta(y, other.y),
      vZ = vZ + delta(z, other.z),
    )
  }
}
