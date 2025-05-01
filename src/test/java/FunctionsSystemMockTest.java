
import org.awaitility.Awaitility;
import org.example.function.FunctionsSystem;
import org.example.logarithmic.Ln;
import org.example.trigonometric.Sin;
import org.example.trigonometric.Tan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.math.BigDecimal.valueOf;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class FunctionsSystemMockTest {

    @Mock
    private Sin sin;

    @Mock
    private Tan tan;

    @Mock
    private Ln ln;

    @InjectMocks
    private FunctionsSystem system;


    @Test
    void testCalculationTimeout() {
        BigDecimal x = valueOf(2.0);
        BigDecimal precision = valueOf(0.0000001); // Высокая точность для долгого вычисления

        AtomicReference<BigDecimal> resultRef = new AtomicReference<>();

        new Thread(() -> resultRef.set(system.calculate(x, precision))).start();

        // Ожидаем не более 1 секунды
        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> resultRef.get() != null);

        assertNotNull(resultRef.get());
    }

    @Test
    void testAsyncExceptionHandling() {
        BigDecimal x = valueOf(0);
        BigDecimal precision = valueOf(0.0001);

        AtomicReference<BigDecimal> resultRef = new AtomicReference<>();
        AtomicReference<Exception> exceptionRef = new AtomicReference<>();

        new Thread(() -> {
            try {
                resultRef.set(system.calculate(x, precision));
            } catch (Exception e) {
                exceptionRef.set(e);
            }
        }).start();

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                resultRef.get() != null || exceptionRef.get() != null);

        // Проверяем, что вернулось DEFAULT_VALUE при ошибке
        assertEquals(Integer.MAX_VALUE, resultRef.get().intValue());
    }
}