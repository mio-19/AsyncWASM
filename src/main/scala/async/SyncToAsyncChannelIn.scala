package async

import chisel3._

class SyncToAsyncChannelIn[T <: Data](A: T) extends Module {
  val io = IO(new Bundle {
    val async = ChannelIn(A)

    val data = Output(A)
    val enable = Output(Bool())
    val busy = Input(Bool())
  })
  val l0 = Module(new Lat(A))
  l0.io.input <> io.async
  val async = Wire(ChannelIn(A))
  l0.io.output <> async
  io.data := io.async.unsafeExtract

  val enable = RegInit(Bool(), false.B)
  io.enable := enable
  val free = Wire(Bool())
  val ack = Wire(Bool())
  async.ack := ack

  when(async.unsafeGotData && free && !io.busy) {
    enable := true.B
  }.otherwise {
    enable := false.B
  }

  // State 0 enable=F free=T ack=F
  // State 1 enable=T free=T ack=F gotData
  // State 1.5 enable=T free=F ack=F gotData
  // State 2 enable=F free=F ack=F gotData
  // State 2.5 enable=F free=F ack=T gotData
  // State 3 enable=F free=F ack=T isRTZ
  // State 3.5 enable=F free=T ack=F isRTZ

  when(reset.asBool) {
    free := true.B // State 0
  }.elsewhen(enable) { // State 1
    free := false.B
  }.elsewhen(async.unsafeIsRTZ) { // State 3
    free := true.B
  }.otherwise {
    free := free
  }
  when(reset.asBool) {
    ack := false.B
  }.elsewhen(!free && !ack) { // State 2
    ack := true.B
  }.elsewhen(async.unsafeIsRTZ) { // State 3
    ack := false.B
  }.otherwise {
    ack := ack
  }
}
class SyncToAsyncInline[T <: Data](A: T)(reset: Reset) extends Bundle {
  val io = Wire(new Bundle {
    val async = ChannelIn(A)

    val data = Output(A)
    val enable = Output(Bool())
    val busy = Input(Bool())
  })
  val l0 = Module(new Lat(A))
  l0.io.input <> io.async
  val async = Wire(ChannelIn(A))
  l0.io.output <> async
  io.data := io.async.unsafeExtract

  val enable = RegInit(Bool(), false.B)
  io.enable := enable
  val free = Wire(Bool())
  val ack = Wire(Bool())
  async.ack := ack

  when(async.unsafeGotData && free && !io.busy) {
    enable := true.B
  }.otherwise {
    enable := false.B
  }

  // State 0 enable=F free=T ack=F
  // State 1 enable=T free=T ack=F gotData
  // State 1.5 enable=T free=F ack=F gotData
  // State 2 enable=F free=F ack=F gotData
  // State 2.5 enable=F free=F ack=T gotData
  // State 3 enable=F free=F ack=T isRTZ
  // State 3.5 enable=F free=T ack=F isRTZ

  when(reset.asBool) {
    free := true.B // State 0
  }.elsewhen(enable) { // State 1
    free := false.B
  }.elsewhen(async.unsafeIsRTZ) { // State 3
    free := true.B
  }.otherwise {
    free := free
  }
  when(reset.asBool) {
    ack := false.B
  }.elsewhen(!free && !ack) { // State 2
    ack := true.B
  }.elsewhen(async.unsafeIsRTZ) { // State 3
    ack := false.B
  }.otherwise {
    ack := ack
  }
}