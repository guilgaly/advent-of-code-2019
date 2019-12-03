package day2

sealed trait InstructionResult {
  def updated: Memory
}

object InstructionResult {
  final case class ProgramContinues(updated: Memory, next: Address)
      extends InstructionResult
  final case class ProgramHalts(updated: Memory) extends InstructionResult
}
