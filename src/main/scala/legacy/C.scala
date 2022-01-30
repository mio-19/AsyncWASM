package legacy

import chisel3._

// require "--no-check-comb-loops"
class C extends RawModule {
  val io = IO(new Bundle {
    val value1 = Input(Bool())
    val value2 = Input(Bool())
    val output = Output(Bool())
  })

  val output = Wire(Bool())

  output := io.value1 && io.output || io.value2 && io.output || io.value1 && io.value2

  io.output := output
}

/*
class C extends BlackBox {
  val io = IO(new Bundle {
    val value1 = Input(Bool())
    val value2 = Input(Bool())
    val output = Output(Bool())
  })
}
*/
/*
module C(
    input wire value1,
    input wire value2,
    output wire output
);

    assign output = value1 && output || value2 && output || value1 && value2;

endmodule
*/
