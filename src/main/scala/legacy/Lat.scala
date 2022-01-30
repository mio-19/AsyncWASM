package legacy

import chisel3._

// Fancy dual rail half latch
// Please Rewrite this module
class Lat[T <: Data](A: T) extends Mod {
  val io = IO(new Bundle {
    val input = ChannelIn(Input(A))

    val output = ChannelOut(Output(A))
  })

  val width = A.getWidth
  val inputACKs = Wire(Vec(width, Bool()))
  val outputZeros = Wire(Vec(width, Bool()))
  val outputOnes = Wire(Vec(width, Bool()))
  io.output.dual.zeros := outputZeros.asTypeOf(A)
  io.output.dual.ones := outputOnes.asTypeOf(A)
  val latch0s = (0 until width).map(i => {
    val latch0 = Module(new Lat0)
    latch0.io.input0 := io.input.dual.zeros.asUInt.apply(i)
    latch0.io.input1 := io.input.dual.ones.asUInt.apply(i)
    inputACKs(i) := latch0.io.inputACK
    outputZeros.apply(i) := latch0.io.output0
    outputOnes.apply(i) := latch0.io.output1
    latch0.io.outputACK := io.output.ack
  })

  // todo: this line has bugs.
  io.input.ack := (0 until width).map(i => inputACKs(i)).reduce(_ && _)
}

class Lat0 extends Mod {
  val io = IO(new Bundle {
    val input0 = Input(Bool())
    val input1 = Input(Bool())
    val inputACK = Output(Bool())

    val output0 = Output(Bool())
    val output1 = Output(Bool())
    val outputACK = Input(Bool())
  })

  val input0 = Mux(reset.asBool, false.B, io.input0)
  val input1 = Mux(reset.asBool, false.B, io.input1)
  val outputACK_not = Mux(reset.asBool, false.B, !io.outputACK)

  val c0 = Module(new C)
  c0.io.value1 := outputACK_not
  c0.io.value2 := input0
  io.output0 := c0.io.output

  val c1 = Module(new C)
  c1.io.value1 := outputACK_not
  c1.io.value2 := input1
  io.output1 := c1.io.output

  io.inputACK := io.output0 || io.output1
}
