package org.example.trigonometric;


import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

/**
 * This class implements the cotangent function as the ratio of
 * the cosine to the sine function. It utilizes the Cos and Sin
 * classes for its computation.
 */
@Slf4j
public class Cot extends LimitedIterationsExpandableFunction {

    private final Cos cos;
    private final Sin sin;

    public Cot() {
        super();
        this.cos = new Cos();
        this.sin = new Sin();
    }


    public Cot(final Cos cos, final Sin sin) {
        super();
        this.cos = cos;
        this.sin = sin;
    }

    /**
     * Calculates the cotangent of a given angle in radians.
     * <p>
     * This method computes the sine and cosine of the input angle
     * and returns the ratio of cosine to sine. It checks for cases
     * where sin(x) is zero, which leads to an undefined cotangent
     * (division by zero).
     * </p>
     *
     * @param x         The angle in radians for which to compute
     *                  the cotangent.
     * @param precision The precision for the calculation, must be
     *                  strictly greater than zero and less than one.
     * @return The cotangent of the input angle x, computed to
     *         the specified precision.
     * @throws ArithmeticException if the provided parameters are
     *                              invalid or if cotangent is undefined (sin(x) = 0).
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision)
            throws ArithmeticException {
        checkValidity(x, precision);

        final BigDecimal sinValue = sin.calculate(x, precision);
        final BigDecimal cosValue = cos.calculate(x, precision);

        if (sinValue.compareTo(ZERO) == 0) {
            log.error("Cotangent is undefined for angle {} (sin(x) = 0).", x);
            throw new ArithmeticException("Cotangent is undefined for angle " + x + " (sin(x) = 0).");
        }

        BigDecimal cotValue = cosValue.divide(sinValue, DECIMAL128.getPrecision(), HALF_EVEN)
                .setScale(precision.scale(), HALF_EVEN);

        log.info("Calculated cotangent of {}: {}", x, cotValue);
        return cotValue;
    }
}