import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import org.example.function.FunctionsSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for the FunctionsSystem class, verifying the behavior of its calculate method.
 */
class FunctionsSystemTest {
    private final static BigDecimal DEFAULT_VALUE=BigDecimal.valueOf(Integer.MAX_VALUE);

    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.00000001");
    private static final int DEFAULT_SCALE = 8;

    /**
     * Tests that the calculate method throws a NullPointerException when passed a null argument.
     */
    @Test
    void shouldNotAcceptNullArgument() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        assertThrows(NullPointerException.class, () -> system.calculate(null, DEFAULT_PRECISION));
    }

    /**
     * Tests that the calculate method throws a NullPointerException when passed a null precision value.
     */
    @Test
    void shouldNotAcceptNullPrecision() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        assertThrows(NullPointerException.class, () -> system.calculate(new BigDecimal(-2), null));
    }

    /**
     * Tests that the calculate method returns DEFAULT_VALUE when the argument is zero.
     */
    @Test
    void shouldNotAcceptZeroArgument() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        assertEquals(DEFAULT_VALUE, system.calculate(ZERO, DEFAULT_PRECISION));
    }
    /**
     * Tests that the calculate method handles negative input values correctly.
     * This test may need to be adjusted based on the expected output for the given input.
     */
    @Test
    void shouldCalculateForNegativeValue() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        final BigDecimal input = new BigDecimal("-5");
        final BigDecimal result = new BigDecimal("11.35007272");
        assertEquals(result, system.calculate(input, DEFAULT_PRECISION));
    }
    /**
     * Tests the behavior when logarithm functions would lead to a division by zero.
     */
    @Test
    void shouldReturnDefaultValueWhenLog10OrLog3IsZero() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        assertEquals(DEFAULT_VALUE, system.calculate(new BigDecimal("0"), DEFAULT_PRECISION));
        assertEquals(DEFAULT_VALUE, system.calculate(new BigDecimal("1"), DEFAULT_PRECISION));
    }

    /**
     * Tests that the calculate method returns DEFAULT_VALUE when results in cotangent being undefined.
     */
    @Test
    void shouldReturnDefaultValueWhenCotIsUndefined() {
        final FunctionsSystem system = FunctionsSystem.getInstance();
        final BigDecimal input = new BigDecimal(Math.PI / 2).negate();
        assertEquals(DEFAULT_VALUE, system.calculate(input,  new BigDecimal("0.000001")));
    }
}