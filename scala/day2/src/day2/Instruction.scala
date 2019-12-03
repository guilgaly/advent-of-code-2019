package day2

sealed trait Instruction

object Instruction {
  sealed trait TwoOperands extends Instruction {
    def left: Address
    def right: Address
    def output: Address
  }

  final case class Addition(left: Address, right: Address, output: Address)
      extends TwoOperands

  final case class Multiplication(
      left: Address,
      right: Address,
      output: Address,
  ) extends TwoOperands

  case object Halt extends Instruction
}
