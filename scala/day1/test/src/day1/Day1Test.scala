package day1

import java.util.Optional

import common.UnitSpec

class Day1Test extends UnitSpec {
  "parseInput" when {
    "given an input file" should {
      "parse each line" in {
        val input = List("12596", "", "256  \t", "  653 ", "", "  \t ")
        val expected = List(12596L, 256L, 653L)
        Day1.parseInput(input) shouldBe expected
      }
    }
  }

  "Part1.fuelByModule" when {
    "given the mass of a module" should {
      "calculate the amount of fuel required to launch it" in {
        import Part1.fuelByModule

        fuelByModule(12) shouldBe 2
        fuelByModule(14) shouldBe 2
        fuelByModule(1969) shouldBe 654
        fuelByModule(100756) shouldBe 33583
      }
    }
  }
Optional.of
  "Part2.fuelByModule" when {
    "given the mass of a module" should {
      "calculate the amount of fuel required to launch it" in {
        import Part2.fuelByModule

        fuelByModule(14) shouldBe 2
        fuelByModule(1969) shouldBe 966
        fuelByModule(100756) shouldBe 50346
      }
    }
  }
}
