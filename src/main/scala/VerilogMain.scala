import async._
import chisel3._
import chisel3.stage.ChiselStage

object VerilogMain extends App {
  val stage = new ChiselStage

  def gen(mod: => RawModule) = stage.emitVerilog(mod, Array("--no-check-comb-loops", "--target-dir", "target/verilog"))

  gen(new C)
  gen(new FD)
  gen(new Lat0)
  gen(new Lat(UInt(32.W)))
  gen(new And)
  gen(new Or)
  gen(new UartTxSync(200000000, 9600))
  gen(new SyncToAsyncChannelIn(UInt(32.W)))
  gen(new UartTx(200000000, 9600))
  gen(new FIFO(64, UInt(32.W)))
}