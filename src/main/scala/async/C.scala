package async

import chisel3._

class C extends Module {
  val io = IO(new Bundle {
    val value1 = Input(Bool())
    val value2 = Input(Bool())
    val output = Output(Bool())
  })

  val output = Reg(Bool())

  output := io.value1 && io.output || io.value2 && io.output || io.value1 && io.value2

  io.output := output
}
