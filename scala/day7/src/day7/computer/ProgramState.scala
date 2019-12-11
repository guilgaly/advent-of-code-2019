package day7.computer

case class ProgramState(
    memory: Memory,
    inputs: List[Value],
    outputs: List[Value], // stored in reverse order
) {
  def updateMemory(index: Address, value: Value): Either[String, ProgramState] =
    if (0 <= index && index < memory.size)
      Right(copy(memory = memory.updated(index, value)))
    else
      Left(s"Memory update out of range at index $index")

  def popInput: Either[String, (Value, ProgramState)] =
    inputs match {
      case head :: tail => Right((head, copy(inputs = tail)))
      case Nil          => Left("No more inputs remaining")
    }

  def pushOutput(output: Value): ProgramState =
    copy(outputs = output :: outputs)

  override def toString: String =
    s"""ProgramState:
       |- memory: ${memory.mkString(",")}
       |- inputs: ${inputs.mkString(",")}
       |- outputs: ${outputs.reverse.mkString(",")}""".stripMargin
}
