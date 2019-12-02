package day1

import common.UnitSpec

class Day1Test extends UnitSpec {
  "fuelByModule" when {
    "given the mass of a module" should {
      "calculate the amount of fuel required to launch it" in {
        Day1.fuelByModule(12) shouldBe 2
        Day1.fuelByModule(14) shouldBe 2
        Day1.fuelByModule(1969) shouldBe 654
        Day1.fuelByModule(100756) shouldBe 33583
      }
    }
  }
  "parseInput" when {
    "given an input file" should {
      "parse each line" in {
        val input = List("12596", "", "256  \t", "  653 ", "", "  \t ")
        val expected = List(12596L, 256L, 653L)
        Day1.parseInput(input) shouldBe expected
      }
    }
  }
}
