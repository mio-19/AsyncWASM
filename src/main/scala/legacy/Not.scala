package legacy

import chisel3._

class Not extends RawModule {
  val io = IO(new Bundle {
    val value = Input(Dual(Bool()))

    val output = Output(Dual(Bool()))
  })
  io.output.ones := io.value.zeros
  io.output.zeros := io.value.ones
}
