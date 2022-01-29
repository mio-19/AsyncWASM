package async

import chisel3._

// Hack ;)
object whenListeningPosedge {
  def apply(signal: => Bool)(cond: => Bool)(block: => Any) =
    withClock(signal.asTypeOf(Clock())) {
      when(cond)(block)
    }

}

object listening {
  def apply(signal1: => Bool, signal2: => Bool)(block: => Any) = {
    val current = VecInit(signal1, signal2)
    val changes = Wire(Bool())
    val fakeClk = changes.asTypeOf(Clock())
    val previous = withClock(fakeClk) {
      Reg(Vec(2, Bool()))
    }
    changes := !(current.asUInt === previous.asUInt)
    withClock(fakeClk) {
      previous := current
      block
    }
  }
}

class WhenListeningTest extends Module {
  val io = IO(new Bundle {
    val value1 = Input(Bool())
    val value2 = Input(Bool())
    val value3 = Input(Bool())
    val value4 = Input(Bool())

    val output = Output(Bool())
  })

  listening(io.value2, io.value3) {
    val output = RegInit(false.B)
    when (io.value4) {
      output := io.value1
    }
    io.output := output
  }
}