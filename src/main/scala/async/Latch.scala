package async

import chisel3._

// Fancy dual rail half latch
class Latch[T <: Data](A: T) extends Module {
  val io = IO(new Bundle {
    val input = Input(Dual(A))
    val inputACK = Output(Bool())

    val output = Output(Dual(A))
    val outputACK = Input(Bool())
  })

  val inputACK = RegInit(false.B)
  io.inputACK := inputACK

  private val width = A.getWidth
  val inputACKs = Wire(Vec(width, Bool()))
  val latch0s = (0 until width).map(i => {
    val latch0 = Module(new Latch0)
    latch0.io.input0 := io.input.zeros.asUInt.apply(i)
    latch0.io.input1 := io.input.ones.asUInt.apply(i)
    latch0.io.inputACK := inputACKs(i)
    latch0.io.output0 := io.output.zeros.asUInt.apply(i)
    latch0.io.output1 := io.output.ones.asUInt.apply(i)
    latch0.io.outputACK := io.outputACK
  })

  // todo: check me
  io.inputACK := (0 until width).map(i => inputACKs(i)).reduce(_ && _)
}

class Latch0 extends Module {
  val io = IO(new Bundle {
    val input0 = Input(Bool())
    val input1 = Input(Bool())
    val inputACK = Output(Bool())

    val output0 = Output(Bool())
    val output1 = Output(Bool())
    val outputACK = Input(Bool())
  })

  val input0 = RegInit(false.B)
  input0 := io.input0
  val input1 = RegInit(false.B)
  input1 := io.input1
  val outputACK_not = RegInit(false.B)
  outputACK_not := !io.outputACK

  val c0 = Module(new C)
  c0.io.value1 := outputACK_not
  c0.io.value2 := input0
  io.output0 := c0.io.output

  val c1 = Module(new C)
  c1.io.value1 := outputACK_not
  c1.io.value2 := io.input1
  io.output1 := c1.io.output

  io.inputACK := io.output0 || io.output1
}
