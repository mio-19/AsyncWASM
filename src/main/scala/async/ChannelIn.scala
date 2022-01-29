package async

import chisel3._

class ChannelIn[T <: Data](A: T) extends Bundle {
  val dual = Input(Dual(A))
  val ack = Output(Bool())
}

object ChannelIn {
  def apply[T <: Data](A: T) = new ChannelIn[T](A)
}

object ChannelOut {
  def apply[T <: Data](A: T) = Flipped(new ChannelIn[T](A))
}