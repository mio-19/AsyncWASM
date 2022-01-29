package async

import chisel3._

class ChannelIn[T <: Data](A: T) extends Bundle {
  val dual = Input(Dual(A))
  val ack = Output(Bool())

  def unsafeIsCleared: Bool = dual.unsafeIsCleared

  def unsafeIsValid: Bool = dual.unsafeIsValid

  // todo: deduplication
  def unsafeIsValidFor1Cycle: Bool = RegNext(this.unsafeIsValid)

  def unsafeExtract: T = dual.unsafeExtract

  def unsafeIsRTZ: Bool = dual.unsafeIsCleared && ack

  def unsafeGotData: Bool = dual.unsafeIsValid && !ack

  def unsafeGotDataFor1Cycle: Bool = this.unsafeIsValidFor1Cycle && !ack

  def unsafeIsRTZFor1Cycle: Bool = !this.unsafeIsValidFor1Cycle && this.unsafeIsRTZ

  // for ChannelOut
  def unsafeWrite(x: T): Unit = dual.unsafeWrite(x)
}

object ChannelIn {
  def apply[T <: Data](A: T) = new ChannelIn[T](A)
}

object ChannelOut {
  def apply[T <: Data](A: T) = Flipped(new ChannelIn[T](A))
}