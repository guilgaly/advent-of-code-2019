package day1

object Part2 {
  def totalFuel(moduleMasses: List[Long]): Long =
    moduleMasses.map(fuelByModule).sum

  def fuelByModule(moduleMass: Long): Long = {
    def fuelByMass(mass: Long) =
      mass / 3 - 2

    def recurs(mass: Long): Long = {
      val baseFuel = fuelByMass(mass)
      if (baseFuel <= 0) 0 else baseFuel + recurs(baseFuel)
    }

    recurs(moduleMass)
  }
}
