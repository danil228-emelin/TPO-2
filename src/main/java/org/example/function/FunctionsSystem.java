package org.example.function;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.ONE;

import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.example.logarithmic.Ln;
import org.example.logarithmic.Log;
import org.example.trigonometric.Cos;
import org.example.trigonometric.Sin;
import org.example.trigonometric.Tan;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class FunctionsSystem implements SeriesExpandableFunction {

    private final Sin sin;
    private final Tan tan;
    private final Cos cos;
    private final BigDecimal cot;
    private final BigDecimal csc;
    private final BigDecimal sec;
    private final Ln ln;
    private final Log log2;
    private final Log log3;
    private final Log log5;
    private final Log log10;

    public FunctionsSystem() {
        this.sin = new Sin();
        this.tan = new Tan();
        this.cos = new Cos();
        this.cot = ZERO;
        this.csc = ZERO;
        this.sec = ZERO;
        this.ln = new Ln();
        this.log2 = new Log(2);
        this.log3 = new Log(3);
        this.log5 = new Log(5);
        this.log10 = new Log(10);
    }

    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision) {
        final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        final BigDecimal correctedX = x.remainder(BigDecimalMath.pi(mc).multiply(new BigDecimal(2)));

        if (x.compareTo(ZERO) <= 0) {
            BigDecimal tanX = tan.calculate(correctedX, precision);
            BigDecimal sinX = sin.calculate(correctedX, precision);
            BigDecimal cosX = cos.calculate(correctedX, precision);
            if (sinX.compareTo(ZERO) == 0) {
                throw new ArithmeticException(format("cotX and csxX are undefined, because sinX = 0 where X = %s", x));
            }

            if (cosX.compareTo(ZERO) == 0) {
                throw new ArithmeticException(format("secX is undefined, because cosX = 0 where X = %s", x));
            }

            BigDecimal cotX = cosX.divide(sinX, RoundingMode.HALF_DOWN);
            BigDecimal cscX = ONE.divide(sinX,RoundingMode.HALF_DOWN);
            BigDecimal secX = ONE.divide(cosX,RoundingMode.HALF_DOWN);

            BigDecimal term1 = tanX.divide(cotX, mc).divide(sinX, mc);
            BigDecimal term2 = sinX.subtract(cosX);
            BigDecimal term3 = cotX.add(cosX.subtract(cosX));
            BigDecimal term4 = cscX.subtract(secX).add(secX);
            BigDecimal term5 = term3.divide(term4, mc);

            return term1.add(term2).subtract(term5).subtract(sinX).setScale(precision.scale(), HALF_EVEN);
        } else {
            BigDecimal log5X = log5.calculate(correctedX, precision);
            BigDecimal log10X = log10.calculate(correctedX, precision);
            BigDecimal lnX = ln.calculate(correctedX, precision);
            BigDecimal log3X = log3.calculate(correctedX, precision);
            BigDecimal log2X = log2.calculate(correctedX, precision);

            BigDecimal term1 = log5X.subtract(log10X).subtract(lnX);
            BigDecimal term2 = term1.divide(log10X, mc).divide(log3X, mc);
            BigDecimal term3 = log2X.subtract(log10X).subtract(lnX);

            return term2.subtract(term3).setScale(precision.scale(), HALF_EVEN);
        }
    }
}
