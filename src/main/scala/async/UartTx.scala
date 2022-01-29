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
  io.txd := u0.txd
  val s0 = Module(new SyncToAsync(UInt(8.W)))
  s0.reset := reset
  u0.io.data := s0.io.data
  u0.io.start := s0.io.enable
  s0.io.busy := u0.io.busy

  s0.async <> io.value
}
