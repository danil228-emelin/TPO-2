package org.example.function;

import ch.obermuhlner.math.big.BigDecimalMath;
import lombok.extern.slf4j.Slf4j;
import org.example.logarithmic.Ln;
import org.example.logarithmic.Log;
import org.example.trigonometric.Cos;
import org.example.trigonometric.Cot;
import org.example.trigonometric.Sin;
import org.example.trigonometric.Tan;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * A class representing a collection of mathematical functions
 * implementing the SeriesExpandableFunction interface. This class provides
 * methods to calculate trigonometric and logarithmic functions,
 * and handles specific cases for calculations.
 */
@Slf4j
public class FunctionsSystem implements SeriesExpandableFunction {
    private final static FunctionsSystem FUNCTIONS_SYSTEM_INSTANCE = new FunctionsSystem();
    // default value in case of arithmetic errors such as division by zero.
    private final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(Integer.MAX_VALUE);
    private final Sin sin;
    private final Tan tan;
    private final Cos cos;
    private final Ln ln;
    private final Log log2;
    private final Log log3;
    private final Log log5;
    private final Log log10;

    private FunctionsSystem() {
        this.sin = Sin.getSin();
        this.tan = new Tan();
        this.cos = Cos.getCos();
        this.ln = Ln.getInstance();
        this.log2 = Log.getLog(2);
        this.log3 = Log.getLog(3);
        this.log5 = Log.getLog(5);
        this.log10 = Log.getLog(10);
    }

    public static FunctionsSystem getInstance() {
        return FUNCTIONS_SYSTEM_INSTANCE;
    }

    /**
     * Calculates a value based on the input x, applying
     * trigonometric and logarithmic functions accordingly, and
     * respecting the specified precision.
     *
     * @param x         The input value for which the calculation
     *                  is to be performed.
     * @param precision The precision to which the calculation
     *                  should be rounded.
     * @return BigDecimal result of the calculation.
     * @throws ArithmeticException if cotangent or secant is undefined.
     */
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision) {

        final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        final BigDecimal correctedX = x.remainder(BigDecimalMath.pi(mc).multiply(new BigDecimal(2)));

        log.info("Starting calculation for x: {} with precision: {}", x, precision);
        log.debug("Corrected x: {}", correctedX);


        if (x.compareTo(ZERO) <= 0) {
            try {


                BigDecimal tanX = tan.calculate(correctedX, precision);
                BigDecimal sinX = sin.calculate(correctedX, precision);
                BigDecimal cosX = cos.calculate(correctedX, precision);

                log.debug("tanX: {}, sinX: {}, cosX: {}", tanX, sinX, cosX);

                if (sinX.compareTo(ZERO) == 0) {
                    log.warn("cotX and cscX are undefined because sinX = 0 at x = {}", x);
                    return DEFAULT_VALUE;
                }

                if (cosX.compareTo(ZERO) == 0) {
                    log.warn("secX is undefined because cosX = 0 at x = {}", x);
                    return DEFAULT_VALUE;
                }
                Cot cot = new Cot(cos, sin);
                BigDecimal cotX = cot.calculate(correctedX, precision);
                BigDecimal cscX = sin.calculateCsc(correctedX, precision);
                BigDecimal secX = cos.calculateSec(correctedX, precision);

                BigDecimal term1 = tanX.divide(cotX, mc).divide(sinX, mc);
                BigDecimal term2 = sinX.subtract(cosX);
                BigDecimal term3 = cotX.add(cosX.subtract(cosX));
                BigDecimal term4 = cscX.subtract(secX).add(secX);

                BigDecimal term5 = term3.divide(term4, mc);
                BigDecimal result = term1.add(term2).subtract(term5).subtract(sinX).setScale(precision.scale(), HALF_EVEN);
                log.debug("Result for x <= 0: {}", result);
                return result;
            } catch (ArithmeticException exception) {
                log.warn(exception.getMessage());
                log.warn("Return default value: {}", DEFAULT_VALUE);
                return DEFAULT_VALUE;
            }
        } else {
            BigDecimal log5X = log5.calculate(correctedX, precision);
            BigDecimal log10X = log10.calculate(correctedX, precision);
            BigDecimal lnX = ln.calculate(correctedX, precision);
            BigDecimal log3X = log3.calculate(correctedX, precision);
            BigDecimal log2X = log2.calculate(correctedX, precision);

            if (log10X.equals(ZERO) || log3X.equals(ZERO)) {
                log.warn("Division by 0 is prohibited, log10X or log3X = 0 at x = {}", x);
                return DEFAULT_VALUE;
            }


            log.debug("log5X: {}, log10X: {}, lnX: {}, log3X: {}, log2X: {}", log5X, log10X, lnX, log3X, log2X);

            BigDecimal term1 = log5X.subtract(log10X).subtract(lnX);
            BigDecimal term2;
            BigDecimal term3;

            try {
                term2 = term1.divide(log10X, mc).divide(log3X, mc);
                term3 = log2X.subtract(log10X).subtract(lnX);
            } catch (ArithmeticException e) {
                log.warn("Division for term2 failed with log3X: {}. Error: {}", log3X, e.getMessage());
                return DEFAULT_VALUE;
            }

            BigDecimal result = term2.subtract(term3).setScale(precision.scale(), HALF_EVEN);
            log.debug("Result for x > 0: {}", result);
            return result;
        }
    }

}