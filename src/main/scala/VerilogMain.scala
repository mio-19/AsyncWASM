import async.FD
import chisel3.stage.ChiselStage

object VerilogMain extends App {
  (new ChiselStage).emitVerilog(new FD, Array("--target-dir", "target/verilog"))
}