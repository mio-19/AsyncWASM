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

  val l0 = Module(new Latch(UInt(8.W)))
  l0.io.input <> io.value
  val value = l0.io.output

  val u0 = Module(new UartTxSync(baudDivisor))
  io.txd := u0.io.txd
  u0.io.data := value.unsafeExtract
  val busy = u0.io.busy
  val start = RegInit(Bool(), false.B)
  u0.io.start := start

  when(start) {
    start := false.B
  }.elsewhen(value.unsafeGotData && !busy) {
    start := true.B
  }

  val startNext = RegNext(start)

  val valueACK = Wire(Bool())
  value.ack := valueACK
  when(reset.asBool) {
    valueACK := false.B
  }.elsewhen(startNext) {
    valueACK := true.B
  }.elsewhen(value.unsafeIsRTZ) {
    valueACK := false.B
  }.otherwise {
    valueACK := valueACK
  }
}
