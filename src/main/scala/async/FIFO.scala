package async

import chisel3._

class FIFO[T <: Data](size: Int, A: T) extends Mod {
  val io = IO(new Bundle {
    val input = ChannelIn(Input(A))

    val output = ChannelOut(Output(A))
  })
  assert(size >= 1)
  var input: ChannelIn[T] = io.input
  for (i <- 1 until size * 2) {
    val latch = Module(new Lat(A))
    latch.io.input <> input
    input = latch.io.output
  }
  val latchEnd = Module(new Lat(A))
  latchEnd.io.input <> input
  latchEnd.io.output <> io.output
}
