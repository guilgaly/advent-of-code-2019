package day9.computer

case class RawInstruction(opcode: Opcode, mode0: Int, mode1: Int, mode2: Int)

object RawInstruction {
  def parse(instructionCode: Int): Either[Err, RawInstruction] =
    Opcode.parse(instructionCode % 100).map { opcode =>
      val mode0 = (instructionCode / 100) % 10;
      val mode1 = (instructionCode / 1000) % 10;
      val mode2 = (instructionCode / 10000) % 10;
      RawInstruction(opcode, mode0, mode1, mode2)
    }
}
