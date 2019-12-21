package intcode

package object computer {
  type Memory = Vector[Long]
  type Address = Int
  type Value = Long
  type Err = String

  private[computer] def valueToAddress(value: Value): Either[String, Address] =
    if (0 <= value && value <= Int.MaxValue)
      Right(value.toInt)
    else
      Left(s"Value $value cannot be converted to an address")
}
