package async

import chisel3._

class ChannelIn[T <: Data](A: T) extends Bundle {
  val dual = Input(Dual(A))
  val ack = Output(Bool())

  def unsafeIsCleared: Bool = dual.unsafeIsCleared

  def unsafeIsValid: Bool = dual.unsafeIsValid

  def unsafeExtract: T = dual.unsafeExtract

  def unsafeIsRTZ: Bool = dual.unsafeIsCleared && ack
}

object ChannelIn {
  def apply[T <: Data](A: T) = new ChannelIn[T](A)
}

object ChannelOut {
  def apply[T <: Data](A: T) = Flipped(new ChannelIn[T](A))
}