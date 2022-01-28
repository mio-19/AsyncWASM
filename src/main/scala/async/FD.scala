package async

import chisel3._

class FD extends RawModule {
  val io = IO(new Bundle {
    val input0 = Input(Bool())
    val input1 = Input(Bool())
    val inputACK = Output(Bool())

    val output0 = Output(Bool())
    val output1 = Output(Bool())
    val outputACK = Input(Bool())
  })

  val outputACK_not = ! io.outputACK

  val c0 = Module(new C)
  c0.io.value1 := outputACK_not
  c0.io.value2 := io.input0
  io.output0 := c0.io.output

  val c1 = Module(new C)
  c1.io.value1 := outputACK_not
  c1.io.value2 := io.input1
  io.output1 := c1.io.output

  io.inputACK := io.output0 || io.output1

}
