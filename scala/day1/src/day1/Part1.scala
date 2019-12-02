package day1

object Part1 {
  def totalFuel(moduleMasses: List[Long]): Long =
    moduleMasses.map(fuelByModule).sum

  def fuelByModule(moduleMass: Long): Long =
    moduleMass / 3 - 2
}
