package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.function.SeriesExpandableFunction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility class for writing data to a CSV file based on calculations from a {@link SeriesExpandableFunction}.
 */
@Slf4j
public class CsvWriter {

    /**
     * Writes calculated values from a series expansion function to a CSV file.
     *
     * @param filename   The name of the file to write the output to.
     * @param function   The function to calculate values (must implement SeriesExpandableFunction).
     * @param from       The starting value (inclusive) for the calculations.
     * @param to         The ending value (inclusive) for the calculations.
     * @param step       The increment for each iteration between `from` and `to`.
     * @param precision   The precision to use while calculating the results.
     * @throws IOException If an I/O error occurs, such as failing to create the file or write to it.
     */
    public static void write(
            final String filename,
            final SeriesExpandableFunction function,
            final BigDecimal from,
            final BigDecimal to,
            final BigDecimal step,
            final BigDecimal precision)
            throws IOException {

        final Path path = Paths.get(filename);
        final File file = new File(path.toUri());

        log.info("Preparing to write CSV data to file: {}", filename);

        if (!file.getParentFile().exists()) {
            log.debug("Creating directories for file: {}", filename);
            if (!file.getParentFile().mkdirs()) {
                log.error("Failed to create directories for file: {}", filename);
                throw new IOException("Failed to create directories for file: " + filename);
            }
        }

        try {
            if (file.exists()) {
                log.debug("Deleting existing file: {}", filename);
                if (!file.delete()) {
                    log.error("Failed to delete existing file: {}", filename);
                    throw new IOException("Failed to delete existing file: " + filename);
                }
            }
        } catch (SecurityException e) {
            log.error("Permission denied while deleting file: {}", filename, e);
            throw new IOException("Permission denied while deleting file: " + filename, e);
        }

        try {
            log.debug("Creating new file: {}", filename);
            if (!file.createNewFile()) {
                log.error("Failed to create new file: {}", filename);
                throw new IOException("Failed to create new file: " + filename);
            }
        } catch (SecurityException e) {
            log.error("Permission denied while creating file: {}", filename, e);
            throw new IOException("Permission denied while creating file: " + filename, e);
        }

        try (PrintWriter printWriter = new PrintWriter(file)) {
            log.info("Writing data to file: {}", filename);
            for (BigDecimal current = from; current.compareTo(to) <= 0; current = current.add(step)) {
                BigDecimal result = function.calculate(current, precision);
                printWriter.println(current + "," + result);
                log.debug("Wrote data to file: {} (current: {}, result: {})", filename, current, result);
            }
            log.info("Successfully wrote data to {}", filename);
        } catch (IOException e) {
            log.error("Failed to write to file: {}", filename, e);
            throw new IOException("Failed to write to file: " + filename, e);
        }
    }
}