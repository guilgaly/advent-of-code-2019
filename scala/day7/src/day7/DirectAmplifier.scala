package day7

import day7.computer.{Computer, Memory, ProgramState, Value}

object DirectAmplifier {
  def thrusterSignal(
      program: Memory,
      phaseSettings: List[Value],
  ): Either[String, Value] = {
    def phaseOutput(
        phaseSetting: Value,
        input: Value,
    ): Either[String, Value] = {
      Computer.executeProgram(program, inputs = List(phaseSetting, input)) match {
        case Right(ProgramState(_, _, output :: _, _, _)) => Right(output)
        case Right(_)                                     => Left(s"No output from phase")
        case Left(err)                                    => Left(err)
      }
    }

    val z: Either[String, Value] = Right(0)
    phaseSettings.foldLeft(z) { (acc, phaseSetting) =>
      acc.flatMap(input => phaseOutput(phaseSetting, input))
    }
  }
}
