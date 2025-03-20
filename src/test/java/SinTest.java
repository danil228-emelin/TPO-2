import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.obermuhlner.math.big.BigDecimalMath;
import java.math.BigDecimal;
import java.math.MathContext;

import org.example.trigonometric.Sin;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Sin class, which computes the sine of an angle.
 * This class contains a series of tests to verify the correctness
 * of the sine calculations for various input values.
 */
class SinTest {

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0001");

    /**
     * Tests the Sin class calculation for an input of zero.
     * The expected sine value for 0 is 0.
     */
    @Test
    void shouldCalculateForZero() {
        final Sin sin = new Sin();
        assertEquals(ZERO.setScale(4, HALF_EVEN), sin.calculate(ZERO, DEFAULT_PRECISION));
    }

    /**
     * Tests sine calculation for the input of π/2 (90 degrees).
     * The expected sine value for π/2 is 1.
     */
    @Test
    void shouldCalculateForPiDividedByTwo() {
        final Sin sin = new Sin();
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg =
                BigDecimalMath.pi(mc).divide(new BigDecimal(2), DECIMAL128.getPrecision(), HALF_EVEN);
        assertEquals(
                ONE.setScale(DEFAULT_PRECISION.scale(), HALF_EVEN), sin.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests sine calculation for the input of 1 (in radians).
     * The expected value is approximately 0.8415.
     */
    @Test
    void shouldCalculateForOne() {
        final Sin sin = new Sin();
        final BigDecimal expected = new BigDecimal("0.8415");
        assertEquals(expected, sin.calculate(ONE, DEFAULT_PRECISION));
    }

    /**
     * Tests periodicity of the sine function.
     * The sine function is periodic, so the expected value for
     * input of -113 should correspond to a sine value determined by
     * its equivalent angle within the standard range.
     */
    @Test
    void shouldCalculateForPeriodic() {
        final Sin sin = new Sin();
        final BigDecimal expected = new BigDecimal("0.0972");
        assertEquals(expected, sin.calculate(new BigDecimal(-113), DEFAULT_PRECISION));
    }
}