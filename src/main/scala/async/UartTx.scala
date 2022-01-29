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
  val busy = u0.io.busy
  val start = RegInit(Bool(), false.B)
  u0.io.start := start
  val data = RegInit(UInt(8.W), 0.U)
  u0.io.data := data

  when(start) {
    start := false.B
  }.elsewhen(value.unsafeGotDataFor1Cycle && !busy) {
    start := true.B
    data := value.unsafeExtract
  }

  val valueACK = Wire(Bool())
  value.ack := valueACK
  when(reset.asBool) {
    valueACK := false.B
  }.elsewhen(start) {
    valueACK := true.B
  }.elsewhen(value.unsafeIsRTZFor1Cycle) {
    valueACK := false.B
  }.otherwise {
    valueACK := valueACK
  }
}
