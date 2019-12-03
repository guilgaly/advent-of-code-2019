package day2

import common.UnitSpec

class Day2Test extends UnitSpec {
  "Computer.parseOperation" when {
    "given a valid program and position" should {
      "parse it" in {
        val program = Vector(1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50)

        Computer.parseInstruction(program, 0) shouldBe
          Right(Instruction.Addition(9, 10, 3))

        Computer.parseInstruction(program, 4) shouldBe
          Right(Instruction.Multiplication(3, 11, 0))

        Computer.parseInstruction(program, 8) shouldBe Right(Instruction.Halt)
      }
    }

    "given an ivalid opcode" should {
      "return an error" in {
        val prog1 = Vector(0, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50)
        Computer.parseInstruction(prog1, 0) shouldBe a[Left[_, _]]

        val prog2 = Vector(100, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50)
        Computer.parseInstruction(prog2, 0) shouldBe a[Left[_, _]]
      }
    }

    "given an ivalid index" should {
      "return an error" in {
        val prog1 = Vector(1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50)
        Computer.parseInstruction(prog1, 12) shouldBe a[Left[_, _]]

        val prog2 = Vector(1, 9, 10)
        Computer.parseInstruction(prog2, 0) shouldBe a[Left[_, _]]

        val prog3 = Vector(1, 9, 10, 12, 2, 3, 11, 0, 99, 30, 40, 50)
        Computer.parseInstruction(prog3, 0) shouldBe a[Left[_, _]]
      }
    }
  }

  "Computer.executeProgram" when {
    "given a program" should {
      "execute it" in {
        def test(prog: Memory, res: Memory) =
          Computer.executeProgram(prog) shouldBe Right(res)

        test(Vector(1, 0, 0, 0, 99), Vector(2, 0, 0, 0, 99))
        test(Vector(2, 3, 0, 3, 99), Vector(2, 3, 0, 6, 99))
        test(Vector(2, 4, 4, 5, 99, 0), Vector(2, 4, 4, 5, 99, 9801))
        test(
          Vector(1, 1, 1, 4, 99, 5, 6, 0, 99),
          Vector(30, 1, 1, 4, 2, 5, 6, 0, 99),
        )
      }
    }
  }
}
