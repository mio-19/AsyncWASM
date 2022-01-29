package async

import chisel3._
import sync.UartTxSync

class UartTx(baudDivisor: Int) extends Module {
  def this(clkHz: Int, baud: Int) {
    this(clkHz / baud)
  }

  val io = IO(new Bundle {
    val value = ChannelIn(UInt(8.W))

    val txd = Output(Bool())
  })

  val u0 = Module(new UartTxSync(baudDivisor))
  io.txd := u0.io.txd
  u0.io.data := io.value.dual.unsafeExtract
  val busy = u0.io.busy
  val start = RegInit(Bool(), false.B)
  u0.io.start := start

  when(start) {
    start := false.B
  }.elsewhen(io.value.dual.unsafeIsValid && !busy && !io.value.ack) {
    start := true.B
  }

  val valueACK = Wire(Bool())
  io.value.ack := valueACK
  val RTZ = io.value.dual.unsafeIsCleared && io.value.ack
  when(reset.asBool) {
    valueACK := false.B
  }.elsewhen(start) {
    valueACK := true.B
  }.elsewhen(RTZ) {
    valueACK := false.B
  }.otherwise {
    valueACK := valueACK
  }
}
