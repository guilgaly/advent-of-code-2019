package day7

import day7.computer.{Computer, Memory, ProgramState, Value}

object Amplifier {
  def thrusterSignal(
      program: Memory,
      phaseSettings: List[Value],
  ): Either[String, Value] = {
    def phaseOutput(
        phaseCount: Int,
        phaseSetting: Value,
        input: Value,
    ): Either[String, Value] = {
      println(s"[Phase $phaseCount] phaseSetting: $phaseSetting, input: $input")
      Computer.executeProgram(program, inputs = List(phaseSetting, input)) match {
        case Right(ProgramState(_, _, output :: _)) =>
          println(s"[Phase $phaseCount] output: $output")
          Right(output)
        case Right(_) =>
          Left(s"No output from phase $phaseCount")
        case Left(err) =>
          Left(s"Error during phase $phaseCount: $err")
      }
    }

    val z: Either[String, Value] = Right(0)
    phaseSettings.zipWithIndex.foldLeft(z) {
      case (acc, (phaseSetting, phaseCount)) =>
        acc.flatMap(input => phaseOutput(phaseCount, phaseSetting, input))
    }
  }
}
