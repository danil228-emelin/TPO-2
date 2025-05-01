
import org.awaitility.Awaitility;
import org.example.function.FunctionsSystem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FunctionsSystemAsyncTest {

    private final FunctionsSystem system = FunctionsSystem.getInstance();

    @Test
    void testAsyncCalculationForPositiveX() {
        BigDecimal x = valueOf(1.5);
        BigDecimal precision = valueOf(0.0001);

        AtomicReference<BigDecimal> resultRef = new AtomicReference<>();

        // Запускаем вычисление в отдельном потоке
        new Thread(() -> resultRef.set(system.calculate(x, precision))).start();

        // Ожидаем результат с таймаутом
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> resultRef.get() != null);

        BigDecimal result = resultRef.get();
        assertNotNull(result);
        assertEquals(-5.07529, result.doubleValue(), 0.0001);
    }

    @Test
    void testAsyncCalculationForNegativeX() {
        BigDecimal x = valueOf(-0.5);
        BigDecimal precision = valueOf(0.0001);

        AtomicReference<BigDecimal> resultRef = new AtomicReference<>();

        new Thread(() -> resultRef.set(system.calculate(x, precision))).start();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> resultRef.get() != null);

        BigDecimal result = resultRef.get();
        assertNotNull(result);
        assertEquals(-2.37768, result.doubleValue(), 0.0001);
    }

    @Test
    void testAsyncCalculationEdgeCase() {
        BigDecimal x = valueOf(0);
        BigDecimal precision = valueOf(0.0001);

        AtomicReference<BigDecimal> resultRef = new AtomicReference<>();

        new Thread(() -> resultRef.set(system.calculate(x, precision))).start();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> resultRef.get() != null);

        BigDecimal result = resultRef.get();
        assertNotNull(result);
        assertEquals(Integer.MAX_VALUE, result.intValue());
    }
}