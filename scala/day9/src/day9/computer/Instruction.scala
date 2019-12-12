package day9.computer

import day9.computer.Params._

sealed trait Instruction[P <: Params] {
  def params: P
}

object Instruction {
  sealed trait With0In1Out extends Instruction[Params0In1Out]
  sealed trait With1In0Out extends Instruction[Params1In0Out]
  sealed trait With2In0Out extends Instruction[Params2In0Out]
  sealed trait With2In1Out extends Instruction[Params2In1Out]

  final case class Addition(params: Params2In1Out)
      extends Instruction.With2In1Out

  final case class Multiplication(params: Params2In1Out)
      extends Instruction.With2In1Out

  final case class Input(params: Params0In1Out) extends Instruction.With0In1Out

  final case class Output(params: Params1In0Out) extends Instruction.With1In0Out

  final case class JumpIfTrue(params: Params2In0Out)
      extends Instruction.With2In0Out

  final case class JumpIfFalse(params: Params2In0Out)
      extends Instruction.With2In0Out

  final case class LessThan(params: Params2In1Out)
      extends Instruction.With2In1Out

  final case class Equals(params: Params2In1Out) extends Instruction.With2In1Out

  final case class RelBaseOffset(params: Params1In0Out)
      extends Instruction.With1In0Out

  case object Halt extends Instruction[Params0.type] {
    override val params: Params.Params0.type = Params0
  }
}
