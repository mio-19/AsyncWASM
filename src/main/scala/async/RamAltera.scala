package async

import chisel3._

class RamAltera2port1clockIP(addrBits: Int, dataBits: Int = 8) extends Bundle {
  val address_a = Input(UInt(addrBits.W))
  val address_b = Input(UInt(addrBits.W))
  val clock = Input(Clock())
  val data_a = Input(UInt(dataBits.W))
  val data_b = Input(UInt(dataBits.W))
  val rden_a = Input(Bool())
  val rden_b = Input(Bool())
  val wren_a = Input(Bool())
  val wren_b = Input(Bool())
  val q_a = Output(UInt(dataBits.W))
  val q_b = Output(UInt(dataBits.W))
}

class RamAltera extends Bundle {

}
