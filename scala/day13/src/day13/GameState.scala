package day13

import intcode.computer.{Computer, Memory, ProgramState, ProgramStatus}

case class GameState(
    screen: Screen,
    programState: ProgramState,
    finished: Boolean,
) {
  def next: Either[String, GameState] =
    if (finished) {
      Left(s"Game already finished")
    } else {
      Computer.resumeProgram(programState, pauseOnOutput = true).flatMap {
        case state if state.status == ProgramStatus.Halted =>
          Right(copy(finished = true))
        case state1 @ ProgramState(_, _, out1 :: _, _, _, _) =>
          for {
            res2 <- nextOutput(state1.resume.flushOutputs)
            (state2, out2) = res2
            res3 <- nextOutput(state2.resume.flushOutputs)
            (state3, out3) = res3

            tile <- Tile.parse(out3)
            updatedScreen = screen.drawTile(Coord(out1, out2), tile)
            updatedState = state3.resume.flushOutputs
          } yield copy(screen = updatedScreen, programState = updatedState)
        case _ =>
          Left("Missing first output")
      }
    }

  private def nextOutput(prog: ProgramState) =
    Computer.resumeProgram(prog, pauseOnOutput = true).flatMap {
      case state @ ProgramState(_, _, out :: _, _, _, _) =>
        Right((state, out))
      case _ =>
        Left("Missing first output")
    }
}

object GameState {
  def init(program: Memory): GameState =
    GameState(
      Screen.init,
      ProgramState(program, List.empty, List.empty),
      finished = false,
    )
}
