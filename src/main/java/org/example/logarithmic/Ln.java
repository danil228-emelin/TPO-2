package org.example.logarithmic;

import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

import static java.lang.String.format;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class implements the natural logarithm (ln) function using a series
 * expansion with a limited number of iterations. It extends the
 * {@link LimitedIterationsExpandableFunction} to provide functionality
 * for computing ln(x) with a specified precision.
 */
@Slf4j
public class Ln extends LimitedIterationsExpandableFunction {
    private final static Ln LN_INSTANCE = new Ln();

    public static Ln getInstance() {
        return LN_INSTANCE;
    }
    private Ln() {
        super();
    }

    /**
     * Calculates the natural logarithm of a given value.
     * <p>
     * This method computes ln(x) using a series expansion. It first checks if
     * the input value is valid (i.e., greater than zero) and then applies a
     * different approach based on whether x is close to 1 or not. The calculation
     * is performed iteratively until the desired precision is achieved or
     * the maximum number of iterations is reached.
     * </p>
     *
     * @param x        The value for which to compute the natural logarithm, must be greater than zero.
     * @param precision The precision for the calculation, must be strictly greater than zero and less than one.
     * @return The natural logarithm of the input value x, computed to the specified precision.
     * @throws ArithmeticException if {@code x} is not greater than zero, or if the precision is not valid.
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision) throws ArithmeticException {
        Objects.requireNonNull(x, "Function argument can not be null");
        Objects.requireNonNull(precision, "Precision can not be null");
        if (precision.compareTo(ZERO) <= 0 || precision.compareTo(ONE) >= 0) {
            throw new ArithmeticException("Precision must be less than one and more than zero");
        }
        if (x.compareTo(ZERO) <= 0) {
            log.error("Function value for argument {} doesn't exist because it is less than or equal to zero.", x);
            throw new ArithmeticException(format("Function value for argument %s doesn't exist", x));
        }
        if (x.compareTo(ONE) == 0) {
            return ZERO;
        }

        double X = x.doubleValue();
        BigDecimal curValue = BigDecimal.ZERO, prevValue;
        int i = 1;

        if (Math.abs(X - 1) <= 1) {
            do {
                prevValue = curValue;
                curValue = curValue.add(
                        (
                                (BigDecimal.valueOf(-1).pow(i - 1))
                                        .multiply(BigDecimal.valueOf(X - 1).pow(i))
                        )
                                .divide(BigDecimal.valueOf(i), precision.scale(), HALF_UP)
                );
                log.debug("Iteration {}: Current Value = {}", i, curValue);
                i++;
            } while (new BigDecimal("0.1").pow(precision.scale()).compareTo((prevValue.subtract(curValue)).abs()) < 0 && i < maxIterations);

            return curValue.add(prevValue).divide(BigDecimal.valueOf(2), HALF_EVEN);
        } else {
            do {
                prevValue = curValue;
                curValue = curValue.add(
                        (
                                BigDecimal.valueOf(-1).pow(i - 1)
                                        .divide(BigDecimal.valueOf(X - 1).pow(i), precision.scale(), HALF_UP)
                        )
                                .divide(BigDecimal.valueOf(i), precision.scale(), HALF_UP)
                );
                log.debug("Iteration {}: Current Value = {}", i, curValue);
                i++;
            } while (new BigDecimal("0.1").pow(precision.scale()).compareTo((prevValue.subtract(curValue)).abs()) < 0 && i < maxIterations);

            // Recursive call to calculate ln(x-1)
            log.debug("Recursively calling calculate for x-1: {}", X - 1);
            curValue = curValue.add(calculate(BigDecimal.valueOf(X - 1), precision));
        }

        BigDecimal result = curValue.setScale(precision.scale(), HALF_EVEN);
        log.info("Final result for ln({}) with precision {}: {}", x, precision, result);
        return result;
    }
}