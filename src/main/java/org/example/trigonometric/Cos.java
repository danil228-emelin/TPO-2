package org.example.trigonometric;

import ch.obermuhlner.math.big.BigDecimalMath;
import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This class implements the cosine function as an extension of
 * {@link LimitedIterationsExpandableFunction}. It uses the sine function
 * to compute the cosine of an angle, taking advantage of the
 * trigonometric identity: cos(x) = sin(π/2 - x).
 */
@Slf4j
public class Cos extends LimitedIterationsExpandableFunction {

    private final Sin sin; // Instance of the sine function
    private final static Cos COS_INSTANCE = new Cos();


    public static Cos getCos(){
        return COS_INSTANCE;

    }

    private Cos() {
        super();
        this.sin = Sin.getSin();
        log.info("Cosine function initialized with default Sin instance.");
    }

    public Cos(final Sin sin) {
        super();
        this.sin = sin;
        log.info("Cosine function initialized with provided Sin instance.");
    }

    /**
     * Calculates the cosine of a given angle in radians.
     * <p>
     * This method first normalizes the input angle to within the range
     * of 0 to 2π, and then applies the sine function to compute the
     * cosine value based on the trigonometric identity:
     * cos(x) = sin(π/2 - x).
     * </p>
     *
     * @param x        The angle in radians for which to compute
     *                 the cosine.
     * @param precision The precision for the calculation, must be
     *                  strictly greater than zero and less than one.
     * @return The cosine of the input angle x, computed to
     *         the specified precision.
     * @throws ArithmeticException if the provided parameters are
     *                              invalid.
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision)
            throws ArithmeticException {
        checkValidity(x, precision);

        final MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
        final BigDecimal correctedX = x.remainder(BigDecimalMath.pi(mc).multiply(new BigDecimal(2)));

        log.debug("Corrected input angle for cosine calculation: {}", correctedX);

        if (correctedX.compareTo(ZERO) == 0) {
            log.info("Input angle is 0 radians, returning cosine value: {}", ONE);
            return ONE;
        }

        final BigDecimal result = sin.calculate(
                BigDecimalMath.pi(mc)
                        .divide(new BigDecimal(2), DECIMAL128.getPrecision(), HALF_EVEN)
                        .subtract(correctedX),
                precision);

        log.info("Calculated cosine of {}: {}", x, result);
        return result.setScale(precision.scale(), HALF_EVEN);
    }
    /**
     * Calculates the secant of a given angle in radians.
     * <p>
     * This method computes the cosine of the input angle and then
     * returns the reciprocal of that value. It checks for cases where
     * cos(x) is zero, which leads to an undefined secant (division by zero).
     * </p>
     *
     * @param x        The angle in radians for which to compute
     *                 the secant.
     * @param precision The precision for the calculation, must be
     *                  strictly greater than zero and less than one.
     * @return The secant of the input angle x, computed to
     *         the specified precision.
     * @throws ArithmeticException if the provided parameters are
     *                              invalid or if secant is undefined (cos(x) = 0).
     */
    public BigDecimal calculateSec(final BigDecimal x, final BigDecimal precision)
            throws ArithmeticException {
        checkValidity(x, precision);

        final BigDecimal cosValue = calculate(x, precision);

        if (cosValue.compareTo(ZERO) == 0) {
            throw new ArithmeticException("Secant is undefined for angle " + x + " (cos(x) = 0).");
        }

        return ONE.divide(cosValue, DECIMAL128.getPrecision(), HALF_EVEN)
                .setScale(precision.scale(), HALF_EVEN);
    }
}
