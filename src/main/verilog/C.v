module C(
    input wire value1,
    input wire value2,
    output wire output
);

    assign output = value1 && output || value2 && output || value1 && value2;

endmodule