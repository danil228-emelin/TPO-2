package org.example;

import org.example.function.FunctionsSystem;
import org.example.logarithmic.Ln;
import org.example.logarithmic.Log;
import org.example.trigonometric.Cos;
import org.example.trigonometric.Sin;
import org.example.trigonometric.Tan;

import java.io.IOException;
import java.math.BigDecimal;

public class App {

    public static void main(String[] args) throws IOException {
        final Cos cos = Cos.getCos();
        CsvWriter.write(
                "./csv/cos.csv",
                cos,
                new BigDecimal(-1),
                new BigDecimal(1),
                new BigDecimal("0.1"),
                new BigDecimal("0.0000000001"));

        final Sin sin = Sin.getSin();
        CsvWriter.write(
                "csv/sin.csv",
                sin,
                new BigDecimal(-1),
                new BigDecimal(1),
                new BigDecimal("0.1"),
                new BigDecimal("0.0000000001"));

        final Tan tan = new Tan();
        CsvWriter.write(
                "csv/tan.csv",
                tan,
                new BigDecimal(-1),
                new BigDecimal(1),
                new BigDecimal("0.1"),
                new BigDecimal("0.0000000001"));

        final Ln ln = Ln.getInstance();
        CsvWriter.write(
                "csv/ln.csv",
                ln,
                new BigDecimal(1),
                new BigDecimal(20),
                new BigDecimal("0.1"),
                new BigDecimal("0.0000000001"));

        final Log log3 = Log.getLog(3);
        CsvWriter.write(
                "csv/log3.csv",
                log3,
                new BigDecimal(1),
                new BigDecimal(20),
                new BigDecimal("0.1"),
                new BigDecimal("0.00000000001"));

        final Log log5 =Log.getLog(5);
        CsvWriter.write(
                "csv/log5.csv",
                log5,
                new BigDecimal(1),
                new BigDecimal(20),
                new BigDecimal("0.1"),
                new BigDecimal("0.00000000001"));

        final Log log10 = Log.getLog(5);
        CsvWriter.write(
                "csv/log10.csv",
                log10,
                new BigDecimal(1),
                new BigDecimal(20),
                new BigDecimal("0.1"),
                new BigDecimal("0.00000000001"));

        final FunctionsSystem func = FunctionsSystem.getInstance();
        CsvWriter.write(
                "csv/func.csv",
                func,
                new BigDecimal(-2),
                new BigDecimal(2),
                new BigDecimal("0.1"),
                new BigDecimal("0.00000000001"));
    }
}