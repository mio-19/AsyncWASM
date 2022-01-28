import async._
import chisel3._
import chisel3.stage.ChiselStage

object VerilogMain extends App {
  val stage = new ChiselStage

  def gen(mod: => RawModule) = stage.emitVerilog(mod, Array("--target-dir", "target/verilog"))

  gen(new FD)
  gen(new Latch0)
  gen(new Latch(UInt(32.W)))
}