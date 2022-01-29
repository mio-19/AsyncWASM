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

class RamRWCmd(addrBits: Int, dataBits: Int = 8) extends Bundle {
  val write = Bool()
  val addr = UInt(addrBits.W)
  val writeData = UInt(dataBits.W)
}

class RamPort(addrBits: Int, dataBits: Int = 8) extends Bundle {
  val cmd = ChannelIn(new RamRWCmd(addrBits, dataBits))
  val readResult = ChannelOut(UInt(dataBits.W))
}

class RamAltera2port1clock(addrBits: Int, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val ipCore = Flipped(new RamAltera2port1clockIP(addrBits, dataBits))

    val port1 = new RamPort(addrBits, dataBits)
    val port2 = new RamPort(addrBits, dataBits)
  })

  val l1 = Module(new Lat(new RamRWCmd(addrBits, dataBits)))
  l1.io.input <> io.port1.cmd
  val port1cmd = l1.io.output
  val l2 = Module(new Lat(new RamRWCmd(addrBits, dataBits)))
  l2.io.input <> io.port2.cmd
  val port2cmd = l2.io.output


  io.ipCore.clock := clock
  io.ipCore.address_a := port1cmd.unsafeExtract.addr
  io.ipCore.data_a := port1cmd.unsafeExtract.writeData
  val port_a_en = RegInit(Bool(), false.B)
  io.ipCore.rden_a := Mux(port_a_en, !port1cmd.unsafeExtract.write, false.B)
  io.ipCore.wren_a := Mux(port_a_en, port1cmd.unsafeExtract.write, false.B)
  io.ipCore.address_b := port2cmd.unsafeExtract.addr
  io.ipCore.data_b := port2cmd.unsafeExtract.writeData
  val port_b_en = RegInit(Bool(), false.B)
  io.ipCore.rden_b := Mux(port_b_en, !port2cmd.unsafeExtract.write, false.B)
  io.ipCore.wren_b := Mux(port_b_en, port2cmd.unsafeExtract.write, false.B)

  when(port1cmd.unsafeGotData) {
    port_a_en := true.B
  } .otherwise {
    port_a_en := false.B
  }

  when(port2cmd.unsafeGotData) {
    port_b_en := true.B
  } .otherwise {
    port_b_en := false.B
  }

}
