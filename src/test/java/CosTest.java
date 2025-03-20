import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ch.obermuhlner.math.big.BigDecimalMath;
import java.math.BigDecimal;
import java.math.MathContext;

import org.example.trigonometric.Cos;
import org.example.trigonometric.Sin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class CosTest {

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0001");

    @Mock
    private Sin mockSin;  // Mock of the Sin class to isolate tests
    @Spy
    private Sin spySin;   // Spy of the Sin class to track interactions without changing behavior

    /**
     * Tests that the calculate method of the Cos class calls the calculate method of Sin.
     */
    @Test
    void shouldCallSinFunction() {
        final Cos cos = new Cos(spySin);
        cos.calculate(new BigDecimal(6), new BigDecimal("0.001"));

        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    /**
     * Tests that the Cos class correctly calculates cosine using a mocked Sin instance.
     *
     * @throws Exception if there's an error during the calculation
     */
    @Test
    void shouldCalculateWithMockSin() {
        final BigDecimal arg = new BigDecimal(5);
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal correctedArg =
                BigDecimalMath.pi(mc)
                        .divide(new BigDecimal(2), DECIMAL128.getPrecision(), HALF_EVEN)
                        .subtract(arg);
        final BigDecimal sinResult = new BigDecimal("0.283662");

        when(mockSin.calculate(eq(correctedArg), any(BigDecimal.class))).thenReturn(sinResult);

        final Cos cos = new Cos(mockSin);

        assertEquals(sinResult, cos.calculate(arg, new BigDecimal("0.000001")));
    }

    /**
     * Tests the Cos class calculation for an input of zero.
     * The expected cosine value for 0 is 1.
     */
    @Test
    void shouldCalculateForZero() {
        final Cos cos = new Cos();
        assertEquals(ONE, cos.calculate(ZERO, DEFAULT_PRECISION));
    }

    /**
     * Tests cosine calculation for the input of π/2 (90 degrees).
     * The expected cosine value for π/2 is 0.
     */
    @Test
    void shouldCalculateForPiDividedByTwo() {
        final Cos cos = new Cos();
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg =
                BigDecimalMath.pi(mc).divide(new BigDecimal(2), DECIMAL128.getPrecision(), HALF_EVEN);
        assertEquals(
                ZERO.setScale(DEFAULT_PRECISION.scale(), HALF_EVEN), cos.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests cosine calculation for the input of 1 (in radians).
     * The expected value is approximately 0.5403.
     */
    @Test
    void shouldCalculateForOne() {
        final Cos cos = new Cos();
        final BigDecimal expected = new BigDecimal("0.5403");
        assertEquals(expected, cos.calculate(ONE, DEFAULT_PRECISION));
    }

    /**
     * Tests periodicity of the cosine function.
     * The cosine function is periodic, so the expected value for input
     * of -543 should correspond to a cosine value determined by its equivalent angle.
     */
    @Test
    void shouldCalculateForPeriodic() {
        final Cos cos = new Cos();
        final BigDecimal expected = new BigDecimal("-0.8797");
        assertEquals(expected, cos.calculate(new BigDecimal(-543), DEFAULT_PRECISION));
    }

    /**
     * Tests secant calculation for zero.
     * The expected sec(0) = 1 (since cos(0) = 1).
     */
    @Test
    void shouldCalculateSecForZero() {
        final Cos cos = new Cos();
        final BigDecimal sec = cos.calculateSec(ZERO, DEFAULT_PRECISION);
        assertEquals(new BigDecimal("1.0000"), sec);
    }

    /**
     * Tests secant calculation for π/3 (60 degrees).
     * Since cos(π/3) = 0.5, sec(π/3) should be 2.
     */
    @Test
    void shouldCalculateSecForPiDividedByThree() {
        final Cos cos = new Cos();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).divide(new BigDecimal(3), MathContext.DECIMAL128.getPrecision(), HALF_EVEN);
        BigDecimal sec = cos.calculateSec(arg, DEFAULT_PRECISION);
        assertEquals(new BigDecimal("2.0000"), sec);
    }
    /**
     * Tests secant calculation for π/2 (90 degrees), which should be undefined.
     * The calculation should throw an ArithmeticException due to cos(π/2) = 0.
     */
    @Test
    void shouldNotCalculateSecForPiDividedByTwo() {
        final Cos cos = new Cos();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128).divide(new BigDecimal(2), MathContext.DECIMAL128.getPrecision(), HALF_EVEN);
        assertThrows(ArithmeticException.class, () -> cos.calculateSec(arg, DEFAULT_PRECISION));
    }
    /**
     * Tests secant calculation for π (180 degrees).
     * Since cos(π) = -1, sec(π) should be -1.
     */
    @Test
    void shouldCalculateSecForPi() {
        final Cos cos = new Cos();
        final BigDecimal arg = BigDecimalMath.pi(MathContext.DECIMAL128);
        assertEquals(new BigDecimal("-1.0000"), cos.calculateSec(arg, DEFAULT_PRECISION));
    }

}