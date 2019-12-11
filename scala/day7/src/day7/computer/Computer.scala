package day7.computer

import scala.annotation.tailrec

object Computer {
  def executeProgram(
      program: Memory,
      inputs: List[Value] = List.empty,
      pauseOnOutput: Boolean = false,
  ): Either[Err, ProgramState] = {
    val initialState = ProgramState(program, inputs, outputs = List.empty)
    resumeProgram(initialState, pauseOnOutput)
  }

  @tailrec
  def resumeProgram(
      currentState: ProgramState,
      pauseOnOutput: Boolean = false,
  ): Either[Err, ProgramState] = {
    executeNextInstruction(currentState, pauseOnOutput) match {
      case Left(err)                                       => Left(err)
      case Right(state: ProgramState) if state.status.stop => Right(state)
      case Right(state)                                    => resumeProgram(state, pauseOnOutput)
    }
  }

  private[day7] def executeNextInstruction(
      state: ProgramState,
      pauseOnOutput: Boolean,
  ): Either[Err, ProgramState] = {
    import Params._

    def getParamValue(param: Param) = param match {
      case Param.Position(address) => findAt(state.memory, address, "param")
      case Param.Immediate(value)  => Right(value)
    }

    def input(params: Params0In1Out) =
      for {
        resInput <- state.popInput
        (value, inputPopped) = resInput
        updatedState <- inputPopped.updateMemory(params.output, value)
      } yield updatedState.incrementAddress(2)

    def output(params: Params1In0Out) =
      getParamValue(params.param0).map { value =>
        val updatedState = state.pushOutput(value).incrementAddress(2)
        if (pauseOnOutput)
          updatedState.copy(status = ProgramStatus.Paused)
        else
          updatedState
      }

    def jump(params: Params2In0Out, predicate: Value => Boolean) =
      for {
        value0 <- getParamValue(params.param0)
        value1 <- getParamValue(params.param1)
        newAddress = if (predicate(value0)) value1 else state.address + 3
      } yield state.copy(address = newAddress)

    def calculate(params: Params2In1Out, operation: (Value, Value) => Value) =
      for {
        value0 <- getParamValue(params.param0)
        value1 <- getParamValue(params.param1)
        result = operation(value0, value1)
        updatedState <- state.updateMemory(params.output, result)
      } yield updatedState.incrementAddress(4)

    def execute(
        instruction: Instruction[_ <: Params],
    ): Either[Err, ProgramState] = {
      instruction match {
        case instr: Instruction.With0In1Out =>
          instr match { case Instruction.Input(params) => input(params) }
        case instr: Instruction.With1In0Out =>
          instr match { case Instruction.Output(params) => output(params) }
        case instr: Instruction.With2In0Out =>
          instr match {
            case Instruction.JumpIfTrue(params)  => jump(params, _ != 0)
            case Instruction.JumpIfFalse(params) => jump(params, _ == 0)
          }
        case instr: Instruction.With2In1Out =>
          instr match {
            case Instruction.Addition(params)       => calculate(params, _ + _)
            case Instruction.Multiplication(params) => calculate(params, _ * _)
            case Instruction.LessThan(params) =>
              calculate(params, (left, right) => if (left < right) 1 else 0)
            case Instruction.Equals(params) =>
              calculate(params, (left, right) => if (left == right) 1 else 0)
          }
        case Instruction.Halt =>
          Right(state.copy(status = ProgramStatus.Halted))
      }
    }

    parseInstruction(state.memory, state.address).flatMap(execute)
  }

  private[day7] def parseInstruction(
      memory: Memory,
      address: Address,
  ): Either[Err, Instruction[_ <: Params]] = {
    def doParseInstruction(
        rawInstruction: RawInstruction,
    ): Either[Err, Instruction[_ <: Params]] = {
      import Instruction._
      import Params._

      def getInstrParam(idx: Int) =
        findAt(memory, address + idx + 1, s"Instruction parameter $idx")

      def parseParams0In1Out =
        getInstrParam(0).map(Params0In1Out.apply)

      def parseParams1In0Out =
        getInstrParam(0).flatMap(
          val0 => Params1In0Out.parse(rawInstruction, val0),
        )

      def parseParams2In0Out =
        for {
          val0 <- getInstrParam(0)
          val1 <- getInstrParam(1)
          params <- Params2In0Out.parse(rawInstruction, val0, val1)
        } yield params

      def parseParams2In1Out =
        for {
          val0 <- getInstrParam(0)
          val1 <- getInstrParam(1)
          val2 <- getInstrParam(2)
          params <- Params2In1Out.parse(rawInstruction, val0, val1, val2)
        } yield params

      rawInstruction.opcode match {
        case Opcode.Add      => parseParams2In1Out.map(Addition.apply)
        case Opcode.Mult     => parseParams2In1Out.map(Multiplication.apply)
        case Opcode.In       => parseParams0In1Out.map(Input.apply)
        case Opcode.Out      => parseParams1In0Out.map(Output.apply)
        case Opcode.JmpIf    => parseParams2In0Out.map(JumpIfTrue.apply)
        case Opcode.JmpIfNot => parseParams2In0Out.map(JumpIfFalse.apply)
        case Opcode.Lt       => parseParams2In1Out.map(LessThan.apply)
        case Opcode.Eq       => parseParams2In1Out.map(Equals.apply)
        case Opcode.End      => Right(Halt)
      }
    }

    for {
      instructionCode <- findAt(memory, address, "instructionCode")
      rawInstruction <- RawInstruction.parse(instructionCode)
      instruction <- doParseInstruction(rawInstruction)
    } yield instruction
  }

  private def findAt(
      memory: Memory,
      index: Address,
      name: String,
  ): Either[String, Int] =
    memory
      .lift(index)
      .toRight(s"$name out of range at index $index")
}
