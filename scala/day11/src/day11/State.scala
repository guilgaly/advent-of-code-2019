package day11

import day11.Orientation._
import intcode.computer._

case class State(
    hull: ShipHull,
    robotPosition: Coord,
    robotOrientation: Orientation,
    programState: ProgramState,
) {
  def next: Either[String, State] =
    firstRun.flatMap {
      case None =>
        Right(
          this.copy(
            programState = programState.copy(status = ProgramStatus.Halted),
          ),
        )
      case Some((out1, prog1)) =>
        for {
          res2 <- secondRun(prog1)
          (out2, prog2) = res2

          color <- Color.fromCode(out1)
          direction <- Direction.fromCode(out2)

//          _ = println(
//            s"out1: $out1, color: $color, out2: $out2, direction: $direction",
//          )

          updatedHull = hull.paint(robotPosition, color)
          updatedOrientation = robotOrientation.turn(direction)
          updatedPosition = robotPosition.move(updatedOrientation)

//          _ = println(
//            s"position: $updatedPosition, orientation: $updatedOrientation",
//          )

          updatedInputs = List.fill(999)(
            updatedHull.getColor(updatedPosition).code,
          )
          updatedProgram = prog2.copy(inputs = updatedInputs)
        } yield State(
          updatedHull,
          updatedPosition,
          updatedOrientation,
          updatedProgram,
        )
    }

  private def firstRun: Either[Err, Option[(Value, ProgramState)]] =
    Computer.resumeProgram(programState, pauseOnOutput = true) match {
      case Left(err) =>
        Left(err)
      case Right(updatedProg) if updatedProg.status == ProgramStatus.Halted =>
        Right(None)
      case Right(
          updatedProg @ ProgramState(
            _,
            _,
            output :: _,
            _,
            _,
            ProgramStatus.Paused,
          ),
          ) =>
        Right(
          Some(
            (
              output,
              updatedProg
                .copy(outputs = List.empty, status = ProgramStatus.Running),
            ),
          ),
        )
      case Right(_) =>
        Left("No output from first run")
    }

  private def secondRun(
      prog: ProgramState,
  ): Either[Err, (Value, ProgramState)] =
    Computer.resumeProgram(prog, pauseOnOutput = true) match {
      case Left(err) =>
        Left(err)
      case Right(
          updatedProg @ ProgramState(
            _,
            _,
            output :: _,
            _,
            _,
            ProgramStatus.Paused,
          ),
          ) =>
        Right(
          (
            output,
            updatedProg
              .copy(outputs = List.empty, status = ProgramStatus.Running),
          ),
        )
      case Right(_) =>
        Left("No output from second run")
    }
}

object State {
  def init(program: Memory, startColor: Color): State = State(
    ShipHull.init,
    Coord(0, 0),
    FaceUp,
    ProgramState(
      program,
      List.fill(999)(startColor.code),
      outputs = List.empty,
    ),
  )
}
