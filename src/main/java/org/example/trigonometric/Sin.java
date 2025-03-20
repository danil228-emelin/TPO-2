package org.example.trigonometric;

import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This class implements the sine function as an extension of
 * {@link LimitedIterationsExpandableFunction}. It calculates
 * the sine of an angle in radians using the Taylor series expansion.
 */
@Slf4j
public class Sin extends LimitedIterationsExpandableFunction {

    /**
     * Constructs a new instance of the Sin class.
     */
    public Sin() {
        super();
        log.info("Sine function initialized.");
    }

    /**
     * Calculates the sine of a given angle in radians.
     * <p>
     * This method normalizes the input angle to the range of
     * [-2π, 2π] and then uses the Taylor series to compute
     * the sine value. The approximation continues until the
     * specified precision is reached.
     * </p>
     *
     * @param x        The angle in radians for which to compute
     *                 the sine.
     * @param precision The precision for the calculation, must be
     *                  strictly greater than zero and less than one.
     * @return The sine of the input angle x, computed to
     *         the specified precision.
     * @throws ArithmeticException if the provided parameters are
     *                              invalid.
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision)
            throws ArithmeticException {
        checkValidity(x, precision);

        double X = x.doubleValue();
        double PI2 = Math.PI * 2; // Calculate 2 * PI
        int i = 0; // Index for the Taylor series
        BigDecimal sum = BigDecimal.ZERO; // Initialize the sum
        BigDecimal prev; // Used to check the convergence of the series

        // Normalize the input angle to the range of [-2π, 2π]
        if (X >= 0) {
            while (X > PI2) {
                X -= PI2;
            }
        } else {
            while (X < -PI2) {
                X += PI2;
            }
        }

        log.debug("Normalized input angle for sine calculation: {}", X);

        // Taylor series calculation for sin(x)
        do {
            prev = sum;
            sum = sum.add(minusOnePow(i).multiply(prod(X, 2 * i + 1)));
            i++;
        } while (new BigDecimal("0.1").pow(precision.scale()).compareTo(prev.subtract(sum).abs()) < 0);

        log.info("Calculated sine of {}: {}", x, sum);
        return sum.setScale(precision.scale(), HALF_EVEN);
    }

    /**
     * Computes (-1)^n for a given integer n.
     *
     * @param n The exponent to compute (-1)^n.
     * @return A BigDecimal representation of (-1)^n.
     */
    private static BigDecimal minusOnePow(int n) {
        return BigDecimal.valueOf(1 - (n % 2) * 2);
    }

    /**
     * Calculates the product of x divided by each integer from 1 to n.
     *
     * @param x The value to be multiplied.
     * @param n The upper limit of the multiplication.
     * @return The cumulative product as a BigDecimal.
     */
    private static BigDecimal prod(double x, int n) {
        BigDecimal accum = BigDecimal.ONE;

        for (int i = 1; i <= n; i++) {
            accum = accum.multiply(new BigDecimal(x / i));
        }

        return accum;
    }
}