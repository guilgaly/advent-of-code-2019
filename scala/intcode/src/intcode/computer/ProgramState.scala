package intcode.computer

case class ProgramState(
    memory: Memory,
    inputs: List[Value],
    outputs: List[Value], // stored in reverse order
    address: Address = 0,
    relativeBase: Address = 0,
    status: ProgramStatus = ProgramStatus.Running,
) {
  def updateMemory(index: Address, value: Value): Either[String, ProgramState] =
    if (index < 0)
      Left(s"Memory update out of range at index $index")
    else if (index < memory.size)
      Right(copy(memory = memory.updated(index, value)))
    else
      Right(copy(memory = memory.padTo(index, 0L) :+ value))

  def incrementAddress(increment: Int): ProgramState =
    copy(address = address + increment)

  def incrementRelativeBase(increment: Int): ProgramState =
    copy(relativeBase = relativeBase + increment)

  def popInput: Either[String, (Value, ProgramState)] =
    inputs match {
      case head :: tail => Right((head, copy(inputs = tail)))
      case Nil          => Left("No more inputs remaining")
    }

  def pushOutput(output: Value): ProgramState =
    copy(outputs = output :: outputs)

  def flushOutputs: ProgramState =
    copy(outputs = List.empty)

  def resume: ProgramState =
    copy(status = ProgramStatus.Running)

  override def toString: String =
    s"""ProgramState:
       |- memory: ${memory.mkString(",")}
       |- inputs: ${inputs.mkString(",")}
       |- outputs: ${outputs.reverse.mkString(",")}""".stripMargin
}

sealed abstract class ProgramStatus(val stop: Boolean)

object ProgramStatus {
  case object Running extends ProgramStatus(stop = false)
  case object Paused extends ProgramStatus(stop = true)
  case object Halted extends ProgramStatus(stop = true)
}
