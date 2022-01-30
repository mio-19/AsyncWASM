package legacy

import chisel3._

class ChannelIn[T <: Data](A: T) extends Bundle {
  val dual = Input(Dual(A))
  val ack = Output(Bool())

  def unsafeIsCleared: Bool = dual.unsafeIsCleared

  def unsafeIsValid: Bool = dual.unsafeIsValid

  def unsafeExtract: T = dual.unsafeExtract

  def unsafeIsRTZ: Bool = dual.unsafeIsCleared && ack

  def unsafeGotData: Bool = dual.unsafeIsValid && !ack

  // for ChannelOut
  def unsafeWrite(x: T): Unit = dual.unsafeWrite(x)
}

object ChannelIn {
  def apply[T <: Data](A: T) = new ChannelIn[T](A)
}

object ChannelOut {
  def apply[T <: Data](A: T) = Flipped(new ChannelIn[T](A))
}

class ChannelCall[T <: Data, U <: Data](A: T, B: U) extends Bundle {
  val in = ChannelIn(A)
  val out = ChannelOut(B)
}

object ChannelCall {
  def apply[T <: Data, U <: Data](A: T, B: U) = new ChannelCall[T, U](A, B)
}