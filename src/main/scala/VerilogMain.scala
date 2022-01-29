import async._
import chisel3._
import chisel3.stage.ChiselStage

object VerilogMain extends App {
  val stage = new ChiselStage

  def gen(mod: => RawModule) = stage.emitVerilog(mod, Array("--no-check-comb-loops", "--target-dir", "target/verilog"))

  gen(new C)
  gen(new FD)
  gen(new Latch0)
  gen(new Latch(UInt(32.W)))
  gen(new And)
  gen(new Or)
  gen(new sync.UartTx(200000000,9600))
}