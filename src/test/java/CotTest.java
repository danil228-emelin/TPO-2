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
import org.example.trigonometric.Cot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the Cot class, which computes the cotangent of an angle.
 * The tests cover scenarios where cotangent is calculated using different
 * combinations of mocked sine and cosine functions, as well as edge cases
 * including special angles.
 */
@ExtendWith(MockitoExtension.class)
class CotTest {

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0001");

    @Mock
    private Sin mockSin;
    @Mock
    private Cos mockCos;
    @Spy
    private Sin spySin;

    /**
     * Tests that the calculate method of the Cot class calls the
     * calculate methods of both Sin and Cos.
     */
    @Test
    void shouldCallSinAndCosFunctions() {
        final Cos cos = new Cos(spySin);
        final Cos spyCos = spy(cos);

        final Cot cot = new Cot(spyCos, spySin);
        cot.calculate(ONE, DEFAULT_PRECISION);

        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    /**
     * Tests cotangent calculation using mocked sine and cosine values.
     * The expected result is calculated based on predefined values
     * for sine and cosine at the input angle of 5 radians.
     */
    @Test
    void shouldCalculateWithMockSinAndMockCos() {
        final BigDecimal arg = new BigDecimal(5);

        when(mockSin.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("-0.958"));
        when(mockCos.calculate(eq(arg), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("0.283"));

        final Cot cot = new Cot(mockCos, mockSin);
        final BigDecimal expectedResult = new BigDecimal("-0.2954");
        assertEquals(expectedResult, cot.calculate(arg, DEFAULT_PRECISION));
    }


    /**
     * Tests the cotangent calculation for an input of zero.
     * The expected result is an ArithmeticException since cot(0) is undefined
     * (sin(0) = 0).
     */
    @Test
    void shouldNotCalculateForZero() {
        final Cot cot = new Cot();
        assertThrows(ArithmeticException.class, () -> cot.calculate(ZERO, DEFAULT_PRECISION));
    }

    /**
     * Tests that calculating cotangent for π (180 degrees) throws an
     * ArithmeticException due to undefined cotangent values at that angle.
     */
    @Test
    void shouldNotCalculateForPi() {
        final Cot cot = new Cot();
        final MathContext mc = new MathContext(DECIMAL128.getPrecision());
        final BigDecimal arg =
                BigDecimalMath.pi(mc);
        assertThrows(ArithmeticException.class, () -> cot.calculate(arg, DEFAULT_PRECISION));
    }


    /**
     * Tests cotangent calculation for an input of 1 (in radians).
     * The expected value is calculated based on sin(1) and cos(1).
     */
    @Test
    void shouldCalculateForOne() {
        final Cot cot = new Cot();
        final BigDecimal expected = new BigDecimal("0.6421");
        assertEquals(expected, cot.calculate(ONE, DEFAULT_PRECISION));
    }

}