package org.example.trigonometric;

import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * This class implements the tangent function as an extension of
 * {@link LimitedIterationsExpandableFunction}. It calculates
 * the tangent of an angle in radians as the ratio of the sine
 * to the cosine function.
 */
@Slf4j
public class Tan extends LimitedIterationsExpandableFunction {

    private final Sin sin;
    private final Cos cos;


    public Tan(final Sin sin, final Cos cos) {
        super();
        this.sin = sin;
        this.cos = cos;
        log.info("Tangent function initialized with provided Sin and Cos instances.");
    }


    public Tan() {
        super();
        this.sin = Sin.getSin();
        this.cos = Cos.getCos();
        log.info("Tangent function initialized with default Sin and Cos instances.");
    }

    /**
     * Calculates the tangent of a given angle in radians.
     * <p>
     * This method uses the sine and cosine functions to compute
     * the tangent value as sin(x) / cos(x). An ArithmeticException
     * is thrown if the cosine of the angle is zero, which would
     * indicate that the tangent function is undefined at that point.
     * </p>
     *
     * @param x         The angle in radians for which to compute
     *                  the tangent.
     * @param precision The precision for the calculation, must be
     *                  strictly greater than zero and less than one.
     * @return The tangent of the input angle x, computed to
     * the specified precision.
     * @throws ArithmeticException if the cosine of the angle is zero
     *                             (undefined tangent) or if the
     *                             provided parameters are invalid.
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision)
            throws ArithmeticException {
        checkValidity(x, precision);

        final BigDecimal sinValue = sin.calculate(x, precision);
        final BigDecimal cosValue = cos.calculate(x, precision);

        log.debug("Calculated sin({}) = {}", x, sinValue);
        log.debug("Calculated cos({}) = {}", x, cosValue);

        // Check if cosValue is zero to prevent division by zero
        if (cosValue.compareTo(ZERO) == 0) {
            String errorMessage = format("Function value for argument %s doesn't exist (cosine is zero)", x);
            log.error(errorMessage);
            throw new ArithmeticException(errorMessage);
        }

        // Calculate tangent as sin(x) / cos(x)
        final BigDecimal result = sinValue.divide(cosValue, DECIMAL128.getPrecision(), HALF_EVEN);
        log.info("Calculated tan({}) = {}", x, result);
        return result.setScale(precision.scale(), HALF_EVEN);
    }
}