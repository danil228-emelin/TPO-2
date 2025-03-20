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
import org.example.trigonometric.Tan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the Tan class, which computes the tangent of an angle.
 * The tests cover scenarios where tangent is calculated using different
 * combinations of mocked sine and cosine functions, as well as edge cases
 * including special angles.
 */
@ExtendWith(MockitoExtension.class)
class TanTest {

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0001");

    @Mock
    private Sin mockSin; // Mocked Sin instance for testing
    @Mock
    private Cos mockCos; // Mocked Cos instance for testing
    @Spy
    private Sin spySin; // Spied Sin instance to track interactions

    /**
     * Tests that the calculate method of the Tan class calls the
     * calculate methods of both Sin and Cos.
     */
    @Test
    void shouldCallSinAndCosFunctions() {
        final Cos cos = new Cos(spySin);
        final Cos spyCos = spy(cos);

        final Tan tan = new Tan(spySin, spyCos);
        tan.calculate(ZERO, DEFAULT_PRECISION);

        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    /**
     * Tests tangent calculation using mocked sine and cosine values.
     * The expected result is calculated based on predefined values
     * for sine and cosine at the input angle of 5 radians.
     */
    @Test
    void shouldCalculateWithMockSinAndMockCos() {
        final BigDecimal arg = new BigDecimal(5);

        when(mockSin.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("-0.95892427"));
        when(mockCos.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("0.28366218"));

        final Tan tan = new Tan(mockSin, mockCos);
        final BigDecimal expectedResult = new BigDecimal("-3.3805");
        assertEquals(expectedResult, tan.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests tangent calculation using a mocked sine value
     * and a real cosine instance.
     */
    @Test
    void shouldCalculateWithMockSin() {
        final BigDecimal arg = new BigDecimal(5);

        when(mockSin.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("-0.95892427"));

        final Tan tan = new Tan(mockSin, new Cos());
        final BigDecimal expectedResult = new BigDecimal("-3.3801");
        assertEquals(expectedResult, tan.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests tangent calculation using a mocked cosine value
     * and a real sine instance.
     */
    @Test
    void shouldCalculateWithMockCos() {
        final BigDecimal arg = new BigDecimal(5);

        when(mockCos.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("0.28366218"));

        final Tan tan = new Tan(new Sin(), mockCos);
        final BigDecimal expectedResult = new BigDecimal("-3.3804");
        assertEquals(expectedResult, tan.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests the tangent calculation for an input of zero.
     * The expected tangent value for 0 is 0.
     */
    @Test
    void shouldCalculateForZero() {
        final Tan tan = new Tan();
        assertEquals(
                ZERO.setScale(DEFAULT_PRECISION.scale(), HALF_EVEN),
                tan.calculate(ZERO, DEFAULT_PRECISION));
    }

    /**
     * Tests that calculating tangent for π/2 (90 degrees) throws an
     * ArithmeticException due to undefined tangent values at that angle.
     */
    @Test
    void shouldNotCalculateForPiDividedByTwo() {
        final Tan tan = new Tan();
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg =
                BigDecimalMath.pi(mc).divide(new BigDecimal(2), DECIMAL128.getPrecision(), HALF_EVEN);
        assertThrows(ArithmeticException.class, () -> tan.calculate(arg, DEFAULT_PRECISION));
    }

    /**
     * Tests tangent calculation for an input of 1 (in radians).
     * The expected value is approximately 1.5575.
     */
    @Test
    void shouldCalculateForOne() {
        final Tan tan = new Tan();
        final BigDecimal expected = new BigDecimal("1.5575");
        assertEquals(expected, tan.calculate(ONE, DEFAULT_PRECISION));
    }

    /**
     * Tests periodicity of the tangent function.
     * The expected value for input of 134 is based on its equivalent
     * angle within the standard periodic range.
     */
    @Test
    void shouldCalculateForPeriodic() {
        final Tan tan = new Tan();
        final BigDecimal expected = new BigDecimal("-1.9101");
        assertEquals(expected, tan.calculate(new BigDecimal(134), DEFAULT_PRECISION));
    }
}