import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final Sin sin = Sin.getSin();
        assertEquals(ZERO.setScale(4, HALF_EVEN), sin.calculate(ZERO, DEFAULT_PRECISION));
    }

    /**
     * Tests sine calculation for the input of π/2 (90 degrees).
     * The expected sine value for π/2 is 1.
     */
    @Test
    void shouldCalculateForPiDividedByTwo() {
        final Sin sin = Sin.getSin();
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
        final Sin sin = Sin.getSin();
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
        final Sin sin = Sin.getSin();
        final BigDecimal expected = new BigDecimal("0.0972");
        assertEquals(expected, sin.calculate(new BigDecimal(-113), DEFAULT_PRECISION));
    }

    /**
     * Tests cosecant calculation for zero.
     * The calculation should throw an ArithmeticException because
     * csc(0) is undefined (since sin(0) = 0).
     */
    @Test
    void shouldNotCalculateCscForZero() {
        final Sin sin = Sin.getSin();
        assertThrows(ArithmeticException.class, () -> sin.calculateCsc(ZERO, DEFAULT_PRECISION));
    }
    /**
     * Tests cosecant calculation for π/6 (30 degrees).
     * Since sin(π/6) = 0.5, csc(π/6) should be 2.
     */
    @Test
    void shouldCalculateCscForPiDividedBySix() {
        final Sin sin = Sin.getSin();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).divide(new BigDecimal(6), MathContext.DECIMAL128.getPrecision(), HALF_EVEN);
        assertEquals(new BigDecimal("2.0000"), sin.calculateCsc(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests cosecant calculation for π/2 (90 degrees).
     * Since sin(π/2) = 1, csc(π/2) should also be 1.
     */
    @Test
    void shouldCalculateCscForPiDividedByTwo() {
        final Sin sin = Sin.getSin();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).divide(new BigDecimal(2), MathContext.DECIMAL128.getPrecision(), HALF_EVEN);
        assertEquals(ONE.setScale(DEFAULT_PRECISION.scale(), HALF_EVEN), sin.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests cosecant calculation for π (180 degrees).
     * The calculation should throw an ArithmeticException because
     * csc(π) is undefined (since sin(π) = 0).
     */
    @Test
    void shouldNotCalculateCscForPi() {
        final Sin sin = Sin.getSin();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128);
        assertThrows(ArithmeticException.class, () -> sin.calculateCsc(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests cosecant calculation for 3π/2 (270 degrees).
     * Since sin(3π/2) = -1, csc(3π/2) should be -1.
     */
    @Test
    void shouldCalculateCscForThreePiDividedByTwo() {
        final Sin sin = Sin.getSin();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).multiply(new BigDecimal(3)).divide(new BigDecimal(2), MathContext.DECIMAL128.getPrecision(), HALF_EVEN);
        assertEquals(new BigDecimal("-1.0000"), sin.calculateCsc(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests periodicity of the cosecant function.
     * Since csc(x) is periodic with a period of 2π, csc(3π) should equal csc(π).
     */
    @Test
    void shouldCalculateCscForPeriodicValue() {
        final Sin sin = Sin.getSin();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).multiply(new BigDecimal(3)); // 3π
        assertThrows(ArithmeticException.class, () -> sin.calculateCsc(arg, DEFAULT_PRECISION));
    }
}