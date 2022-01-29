package async

import chisel3._
import sync.UartTxSync

class UartTx(baudDivisor: Int) extends Module {
  def this(clkHz: Int, baud: Int) {
    this(clkHz / baud)
  }
  val io = IO(new Bundle {
    val value = Input(Dual(UInt(8.W)))
    val valueACK = Output(Bool())

    val txd = Output(Bool())
  })

  val u0 = Module(new UartTxSync(baudDivisor))
  io.txd := u0.io.txd
  u0.io.data := io.value.unsafeExtract
  val busy = u0.io.busy
  val start = RegInit(Bool(), false.B)
  u0.io.start := start

  when(start) {
    start := false.B
  } .elsewhen(io.value.unsafeIsValid && !busy && !io.valueACK) {
    start := true.B
  }

  val valueACK = Wire(Bool())
  io.valueACK := valueACK
  val RTZ = io.value.unsafeIsCleared && io.valueACK
  // todo: emulate always @(posedge reset, posedge start, posedge RTZ)
  when (start) {
    valueACK := true.B
  } .elsewhen(RTZ || reset.asBool) {
    valueACK := false.B
  } .otherwise {
    valueACK := valueACK
  }
}
