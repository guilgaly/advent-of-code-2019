package day7.computer

sealed trait Params

object Params {
  final case object Params0 extends Params

  final case class Params0In1Out(output: Address) extends Params

  final case class Params1In0Out(param0: Param) extends Params
  object Params1In0Out {
    def parse(
        rawInstruction: RawInstruction,
        val0: Int,
    ): Either[Err, Params1In0Out] =
      Param.parse(val0, rawInstruction.mode0).map(Params1In0Out.apply)
  }

  final case class Params2In0Out(param0: Param, param1: Param) extends Params
  object Params2In0Out {
    def parse(
        rawInstruction: RawInstruction,
        val0: Int,
        val1: Int,
    ): Either[Err, Params2In0Out] =
      for {
        param0 <- Param.parse(val0, rawInstruction.mode0)
        param1 <- Param.parse(val1, rawInstruction.mode1)
      } yield Params2In0Out(param0, param1)
  }

  final case class Params2In1Out(param0: Param, param1: Param, output: Address)
      extends Params
  object Params2In1Out {
    def parse(
        rawInstruction: RawInstruction,
        val0: Int,
        val1: Int,
        val2: Int,
    ): Either[Err, Params2In1Out] =
      for {
        param0 <- Param.parse(val0, rawInstruction.mode0)
        param1 <- Param.parse(val1, rawInstruction.mode1)
      } yield Params2In1Out(param0, param1, val2)
  }
}
