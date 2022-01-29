package async

import chisel3._
import chisel3.util.MixedVecInit

// based on https://github.com/pConst/basic_verilog/blob/a1608c2326c057dbe4831e7e5e68ee2c1a769f9b/uart_tx.sv
class UartTxSync(baudDivisor: Int) extends Module {
  def this(clkHz: Int, baud: Int) {
    this(clkHz / baud)
  }

  val io = IO(new Bundle {
    val data = Input(UInt(8.W))
    val start = Input(Bool())

    val busy = Output(Bool())
    val txd = Output(Bool())
  })

  val shifter = RegInit(UInt(10.W), 0.U)
  val txd = RegInit(Bool(), true.B)
  io.txd := txd

  val shifterTxd = Wire(new Bundle {
    val shifter = UInt(10.W)
    val txd = Bool()
  })
  shifterTxd.shifter := shifter
  shifterTxd.txd := txd

  val sampleCntr = RegInit(UInt(16.W), (baudDivisor - 1).U)

  when(sampleCntr === 0.U) {
    sampleCntr := (baudDivisor - 1).U
  }
    .otherwise {
      sampleCntr := sampleCntr - 1.U
    }

  val doSample = sampleCntr === 0.U

  val busy = RegInit(Bool(), false.B)
  io.busy := busy

  val data = Wire(UInt(8.W))
  data := io.data
  when(!busy) {
    when(io.start) {
      shifter := MixedVecInit(true.B, data, false.B).asTypeOf(UInt(10.W))
      busy := true.B
    }
  }
    .otherwise {
      when(doSample) {
        shifterTxd := (shifterTxd.asUInt >> 1).asTypeOf(shifterTxd.cloneType);
        when(VecInit(shifter.asBools.slice(1, 10)).asUInt === 0.U) {
          busy := false.B
        }
      }
    }
}
