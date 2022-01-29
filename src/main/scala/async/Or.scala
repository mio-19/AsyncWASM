package async

import chisel3._

class Or extends RawModule {
  val io = IO(new Bundle {
    val value1 = Input(Dual(Bool()))
    val value2 = Input(Dual(Bool()))

    val output = Output(Dual(Bool()))
  })

  val not1 = Module(new Not)
  not1.io.value := io.value1
  val not2 = Module(new Not)
  not2.io.value := io.value2

  val and = Module(new And)
  and.io.value1 := not1.io.value
  and.io.value2 := not2.io.value

  val not3 = Module(new Not)
  not3.io.value := and.io.output

  io.output := not3.io.value
}
