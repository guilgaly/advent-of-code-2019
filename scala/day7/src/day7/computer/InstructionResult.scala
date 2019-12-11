package day7.computer

sealed trait InstructionResult {
  def state: ProgramState
}

object InstructionResult {
  final case class ProgramContinues(state: ProgramState, next: Address)
      extends InstructionResult
  final case class ProgramHalts(state: ProgramState) extends InstructionResult
}
