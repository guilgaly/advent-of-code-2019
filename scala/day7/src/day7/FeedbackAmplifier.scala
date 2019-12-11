package day7

import day7.computer._

object FeedbackAmplifier {
  def thrusterSignal(
      program: Memory,
      phaseSettings: List[Value],
  ): Either[String, Value] = {
    import PhaseResult._

    def phaseOutput(
        phases: List[ProgramState],
    ): Either[String, PhaseResult] = {
      val currentPhase = phases.head
      val nextPhase = phases.tail.head
      val otherPhases = phases.tail.tail

      Computer.resumeProgram(currentPhase, pauseOnOutput = true) match {
        case Left(err) =>
          Left(err)
        case Right(ProgramState(_, input :: _, _, _, ProgramStatus.Halted)) =>
          Right(Finished(input))
        case Right(
            res @ ProgramState(_, _, output :: _, _, _),
            ) =>
          val updatedNextPhase = nextPhase.copy(
            inputs = nextPhase.inputs :+ output,
            outputs = List.empty,
            status = ProgramStatus.Running,
          )
          val updatedPhases = updatedNextPhase +: otherPhases :+ res
          Right(Continue(updatedPhases))
        case Right(_) =>
          Left(s"No output from phase")
      }
    }

    @scala.annotation.tailrec
    def recurs(phases: List[ProgramState]): Either[String, Value] = {
      phaseOutput(phases) match {
        case Left(err)                      => Left(err)
        case Right(Finished(output))        => Right(output)
        case Right(Continue(updatedPhases)) => recurs(updatedPhases)
      }
    }

    val initInputs =
      List(phaseSettings.head, 0) +: phaseSettings.tail.map(List(_))
    val initPhases = initInputs.map { inputs =>
      ProgramState(program, inputs, outputs = List.empty)
    }
    recurs(initPhases)
  }

  sealed private trait PhaseResult
  private object PhaseResult {
    case class Continue(phases: List[ProgramState]) extends PhaseResult
    case class Finished(output: Value) extends PhaseResult
  }
}
