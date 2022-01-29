package async

import chisel3._

class And extends RawModule {
  val io = IO(new Bundle {
    val value1 = Input(Dual(Bool()))
    val value2 = Input(Dual(Bool()))

    val output = Output(Dual(Bool()))
  })

  val a0b0 = Module(new C)
  a0b0.io.value1 := io.value1.zeros
  a0b0.io.value2 := io.value2.zeros
  val a0b1 = Module(new C)
  a0b1.io.value1 := io.value1.zeros
  a0b1.io.value2 := io.value2.ones
  val a1b1 = Module(new C)
  a1b1.io.value1 := io.value1.ones
  a1b1.io.value2 := io.value2.ones
  val a1b0 = Module(new C)
  a1b0.io.value1 := io.value1.ones
  a1b0.io.value2 := io.value2.zeros

  io.output.ones := a1b1.io.output
  io.output.zeros := a0b0.io.output || a1b0.io.output || a0b1.io.output
}
