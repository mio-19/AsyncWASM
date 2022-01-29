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

  io.enable := enable
  val free = Wire(Bool())
  val ack = Wire(Bool())
  async.ack := ack

  val enable = RegInit(Bool(), false.B)
  withReset(!free) {
    when(async.unsafeGotData && free && !io.busy) {
      enable := true.B
    }.otherwise {
      enable := false.B
    }
  }

  // State 0 enable=F free=T ack=F
  // State 1 enable=T* free=T ack=F gotData
  // State 1.5 enable=T free=F* ack=F gotData
  // State 2 enable=F* free=F ack=F gotData
  // State 2.5 enable=F free=F ack=T* gotData
  // State 3 enable=F free=F ack=T isRTZ*
  // State 3.5 enable=F free=T* ack=F* isRTZ - bit flipping order does not matter

  when(reset.asBool) {
    free := true.B // State 0
  }.elsewhen(enable && free && !ack) { // State 1
    free := false.B
  }.elsewhen(!free && ack && async.unsafeIsRTZ) { // State 3
    free := true.B
  }.otherwise {
    free := free
  }
  when(reset.asBool) {
    ack := false.B
  }.elsewhen(!free && !ack) { // State 2
    ack := true.B
  }.elsewhen(!free && ack && async.unsafeIsRTZ) { // State 3
    ack := false.B
  }.otherwise {
    ack := ack
  }
}
class SyncToAsyncChannelOut[T <: Data](A: T) extends Module {
  val io = IO(new Bundle {
    val async = ChannelOut(A)

    val data = Input(A)
    val enable = Input(Bool())
    val busy = Output(Bool())
  })
  val dataEn = Wire(Bool())
  val data = Reg(A)
  val ack = io.async.ack
  io.async.dual := Mux(dataEn, Dual.from(data), 0.U.asTypeOf(Dual(A)))
  val storeFlag = RegInit(Bool(), false.B)
  val previousFlag = Wire(Bool())
  when (enable && busy && !running && !ack && !dataEn) {// State 1
    storeFlag := !storeFlag
    data := io.data
  }

  val busy = Wire(Bool())
  val running = Wire(Bool())
  io.busy := busy
  val enable = io.enable

  // State 0 enable=F busy=F ack=F dataEn=F
  // State 1 enable=T busy=T ack=F dataEn=F
  // State 1.2 enable=T busy=T ack=F dataEn=T on clock posedge ; get data ; storeFlag = !storeFlag
  // State 1.2 enable=T busy=T ack=F dataEn=T
  // State 1.5 enable=? busy=T ack=F dataEn=T
  // State 2 enable=? busy=T ack=T dataEn=T
  // State 2.5 enable=? busy=T ack=T dataEn=F previousFlag=storeFlag
  // State 3 enable=? busy=T ack=F dataEn=F
  // State 3.5 enable=? busy=F ack=F dataEn=F

  val stored = storeFlag
  when(reset.asBool) {
    previousFlag := storeFlag
  }

  // todo
}
