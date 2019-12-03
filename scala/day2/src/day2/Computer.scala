package day2

import day2.Opcode.{Add, End, Mult}
import day2.Instruction.{Addition, Halt, Multiplication}
import day2.InstructionResult.{ProgramContinues, ProgramHalts}

import scala.annotation.tailrec

object Computer {
  def executeProgram(program: Memory): Either[Err, Memory] = {
    @tailrec
    def recurs(
        currentMemory: Memory,
        currentAddress: Address,
    ): Either[Err, Memory] =
      executeNextInstruction(currentMemory, currentAddress) match {
        case Left(err)                    => Left(err)
        case Right(ProgramHalts(updated)) => Right(updated)
        case Right(ProgramContinues(updatedMemory, nextAddress)) =>
          recurs(updatedMemory, nextAddress)
      }
    recurs(program, 0)
  }

  private[day2] def executeNextInstruction(
      memory: Memory,
      address: Address,
  ): Either[Err, InstructionResult] = {
    def execute(instruction: Instruction): InstructionResult = instruction match {
      case twoOpInstr: Instruction.TwoOperands =>
        val left = memory(twoOpInstr.left)
        val right = memory(twoOpInstr.right)
        val opResult = twoOpInstr match {
          case _: Addition       => left + right
          case _: Multiplication => left * right
        }
        ProgramContinues(memory.updated(twoOpInstr.output, opResult), address + 4)
      case Halt =>
        ProgramHalts(memory)
    }

    parseInstruction(memory, address).map(execute)
  }

  private[day2] def parseInstruction(
      memory: Memory,
      address: Address,
  ): Either[Err, Instruction] = {
    def findAt(index: Address, name: String) =
      memory
        .lift(index)
        .toRight(Err(address, s"$name out of range at index $index"))

    def parseOperands =
      for {
        leftAddress <- findAt(address + 1, "left operand reference")
        rightAddress <- findAt(address + 2, "right operand reference")
        targetAddress <- findAt(address + 3, "output position reference")

        _ <- findAt(leftAddress, "left operand")
        _ <- findAt(rightAddress, "right operand")
        _ <- findAt(targetAddress, "output")
      } yield (leftAddress, rightAddress, targetAddress)

    def doParseInstruction(opcode: Opcode) = opcode match {
      case Add  => parseOperands.map((Addition.apply _).tupled)
      case Mult => parseOperands.map((Multiplication.apply _).tupled)
      case End  => Right(Halt)
    }

    for {
      code <- findAt(address, "opcode position")
      opcode <- Opcode.parse(code).left.map(Err(address, _))
      instruction <- doParseInstruction(opcode)
    } yield instruction
  }
}
