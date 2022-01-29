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

  val l0 = Module(new Lat(UInt(8.W)))
  l0.io.input <> io.value
  val value = l0.io.output

  val u0 = Module(new UartTxSync(baudDivisor))
  io.txd := u0.io.txd
  val busy = u0.io.busy
  val start = RegInit(Bool(), false.B)
  u0.io.start := start
  u0.io.data := value.unsafeExtract

  val enStart = Wire(Bool())
  when(start) {
    start := false.B
  }.elsewhen(value.unsafeGotData && enStart && !busy) {
    start := true.B
  }

  // State 0 start=F enStart=T valueACK=F
  // State 1 start=T enStart=T valueACK=F gotData
  // State 1.5 start=T enStart=F valueACK=F gotData
  // State 2 start=F enStart=F valueACK=F gotData
  // State 2.5 start=F enStart=F valueACK=T gotData
  // State 3 start=F enStart=F valueACK=T isRTZ
  // State 3.5 start=F enStart=T valueACK=F isRTZ
  when(reset.asBool) {
    enStart := true.B // State 0
  }.elsewhen(start) { // State 1
    enStart := false.B
  }.elsewhen(value.unsafeIsRTZ) { // State 3
    enStart := true.B
  }.otherwise {
    enStart := enStart
  }
  val valueACK = Wire(Bool())
  value.ack := valueACK
  when(reset.asBool) {
    valueACK := false.B
  }.elsewhen(!start && !enStart) { // State 2
    valueACK := true.B
  }.elsewhen(value.unsafeIsRTZ) { // State 3
    valueACK := false.B
  }.otherwise {
    valueACK := valueACK
  }
}
