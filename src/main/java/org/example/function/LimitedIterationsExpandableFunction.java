package org.example.function;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An abstract base class for functions that can be expanded as a series,
 * limiting the number of iterations used in the computation. This class
 * implements the SeriesExpandableFunction interface.
 */
public abstract class LimitedIterationsExpandableFunction implements SeriesExpandableFunction {

    private static final int DEFAULT_MAX_ITERATIONS = 1000;

    /**
     * The maximum number of iterations allowed for calculations.
     */
    protected final int maxIterations;


    protected LimitedIterationsExpandableFunction() {
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
    }

    /**
     * Validates the input parameters for the function.
     * <p>
     * This method checks that the input value <code>x</code> and the
     * <code>precision</code> are not null and that the precision is
     * strictly between 0 and 1.
     * </p>
     *
     * @param x          The input value for the function, must not be null.
     * @param precision  The precision for the calculation, must be
     *                   greater than zero and less than one.
     * @throws NullPointerException if either <code>x</code> or
     *                               <code>precision</code> is null.
     * @throws ArithmeticException   if <code>precision</code> is not strictly
     *                               between 0 and 1.
     */
    protected void checkValidity(final BigDecimal x, final BigDecimal precision) {
        Objects.requireNonNull(x, "Function argument can not be null");
        Objects.requireNonNull(precision, "Precision can not be null");
        if (precision.compareTo(ZERO) <= 0 || precision.compareTo(ONE) >= 0) {
            throw new ArithmeticException("Precision must be less than one and more than zero");
        }
    }
}