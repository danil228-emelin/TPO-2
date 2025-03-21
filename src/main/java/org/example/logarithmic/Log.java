package org.example.logarithmic;

import lombok.extern.slf4j.Slf4j;
import org.example.function.LimitedIterationsExpandableFunction;

import java.math.BigDecimal;
import java.util.HashMap;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * This class represents a logarithmic function with a specified base,
 * allowing the calculation of log_b(x) using the natural logarithm (ln)
 * function. It extends the {@link LimitedIterationsExpandableFunction}
 * to provide functionality for computing logarithms with a defined precision.
 */
@Slf4j
public class Log extends LimitedIterationsExpandableFunction {

    private final static HashMap<Integer, Log> FACTORY_LOG = new HashMap<>();
    private final Ln ln; // Instance of natural logarithm class
    private final int base; // Base of the logarithm

    private Log(final int base) {
        super();
        this.ln = Ln.getInstance();
        this.base = base;
        log.info("Log base {} initialized.", base);
    }

    public static Log getLog(Integer base) {
        if (FACTORY_LOG.containsKey(base)) {
            return FACTORY_LOG.get(base);
        } else {
            Log log1 = new Log(base);
            FACTORY_LOG.put(base, log1);
            return log1;
        }
    }

    /**
     * Calculates the logarithm of a given value with a specified base.
     * <p>
     * This method calculates log_b(x) using the formula: log_b(x) = ln(x) / ln(base).
     * It first checks if the input value is valid (i.e., x > 0) and then performs the calculation.
     * </p>
     *
     * @param x         The value for which to compute the logarithm, must be greater than zero.
     * @param precision The precision for the calculation, must be strictly greater than zero and less than one.
     * @return The logarithm of the input value x to the specified base, computed to the specified precision.
     * @throws ArithmeticException if {@code x} is not greater than zero, or if the precision is not valid.
     */
    @Override
    public BigDecimal calculate(final BigDecimal x, final BigDecimal precision) throws ArithmeticException {
        checkValidity(x, precision);

        if (x.compareTo(ZERO) <= 0) {
            log.error("Function value for argument {} doesn't exist because it is less than or equal to zero.", x);
            throw new ArithmeticException(format("Function value for argument %s doesn't exist", x));
        }

        // Calculate ln(x) and ln(base) for logarithm calculation
        BigDecimal lnX = ln.calculate(x, precision);
        BigDecimal lnBase = ln.calculate(new BigDecimal(base), precision);

        log.debug("Calculating log_{}({}) = ln({}) / ln({})", base, x, x, base);
        log.debug("Computed ln({}) = {}", x, lnX);
        log.debug("Computed ln({}) = {}", base, lnBase);

        final BigDecimal result = lnX.divide(lnBase, DECIMAL128.getPrecision(), HALF_EVEN);
        log.info("Calculated log_{}({}) = {}", base, x, result.setScale(precision.scale(), HALF_EVEN));
        return result.setScale(precision.scale(), HALF_EVEN);
    }
}