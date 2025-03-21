import lombok.extern.slf4j.Slf4j;
import org.example.function.FunctionsSystem;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the database schema and functionality.
 * This class uses Testcontainers to spin up a PostgreSQL container
 * and Flyway for database migrations.
 */
@Slf4j
@Testcontainers
public class DatabaseIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    private static Connection connection;
    private static Flyway flyway;
    private final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(Integer.MAX_VALUE);

    /**
     * Starts the PostgreSQL container and sets up Docker host properties.
     */
    @BeforeAll
    static void setContainer() {
        System.setProperty("DOCKER_HOST", "unix:///var/run/docker.sock");
        System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");
        postgres.start();
    }

    /**
     * Configures Flyway for database migrations and establishes a connection to the database.
     */
    @BeforeAll
    static void setUp() {
        // Configure Flyway
        flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .load();
        flyway.migrate();

        // Establish a connection to the database
        try {
            connection = DriverManager.getConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword()
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void resetDatabase() {
        // Clean the database (removes all objects)
        flyway.clean();

        // Reapply the migrations to restore the schema
        flyway.migrate();
    }

    /**
     * Closes the database connection and stops the PostgreSQL container after all tests are executed.
     */
    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
        postgres.stop();
    }

    /**
     * Tests the database connection by verifying that the connection is valid.
     */
    @Test
    void testDatabaseConnection() {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            assertTrue(connection.isValid(5));
        } catch (SQLException e) {
            fail("Failed to connect to the database: " + e.getMessage());
        }
    }

    /**
     * Tests CRUD operations on the 'calculations' table.
     */
    @Test
    void testCrudOperations() throws Exception {
        String insertQuery = "INSERT INTO calculations (x_value, result) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setBigDecimal(1, new BigDecimal("1.0"));
            insertStmt.setBigDecimal(2, new BigDecimal("2.0"));
            insertStmt.executeUpdate();
        }

        // Read data
        String selectQuery = "SELECT result FROM calculations WHERE x_value = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setBigDecimal(1, new BigDecimal("1.0"));
            ResultSet resultSet = selectStmt.executeQuery();
            assertTrue(resultSet.next());
            assertEquals(new BigDecimal("2.0"), resultSet.getBigDecimal("result"));
        }

        // Update data
        String updateQuery = "UPDATE calculations SET result = ? WHERE x_value = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setBigDecimal(1, new BigDecimal("3.0"));
            updateStmt.setBigDecimal(2, new BigDecimal("1.0"));
            updateStmt.executeUpdate();
        }

        // Verify update
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setBigDecimal(1, new BigDecimal("1.0"));
            ResultSet resultSet = selectStmt.executeQuery();
            assertTrue(resultSet.next());
            assertEquals(new BigDecimal("3.0"), resultSet.getBigDecimal("result"));
        }

        // Delete data
        String deleteQuery = "DELETE FROM calculations WHERE x_value = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setBigDecimal(1, new BigDecimal("1.0"));
            deleteStmt.executeUpdate();
        }

        // Verify deletion
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setBigDecimal(1, new BigDecimal("1.0"));
            ResultSet resultSet = selectStmt.executeQuery();
            assertFalse(resultSet.next());
        }
    }

    /**
     * Tests the rollback of migrations by cleaning the database and reapplying migrations.
     */
    @Test
    void testMigrationRollback() throws Exception {
        // Clean the database (removes all objects)
        flyway.clean();

        // Verify the table no longer exists
        String checkTableQuery = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'calculations')";
        try (PreparedStatement checkTableStmt = connection.prepareStatement(checkTableQuery)) {
            ResultSet resultSet = checkTableStmt.executeQuery();
            assertTrue(resultSet.next());
            assertFalse(resultSet.getBoolean(1));
        }

        // Reapply the migration
        flyway.migrate();
    }

    /**
     * Tests inserting and retrieving a calculation result from the database.
     */
    @Test
    void testInsertAndRetrieveCalculationResult() throws Exception {
        FunctionsSystem functionsSystem = FunctionsSystem.getInstance();

        // Perform a calculation
        BigDecimal x = new BigDecimal("1.0");
        BigDecimal precision = new BigDecimal("0.0000000001");
        BigDecimal result = functionsSystem.calculate(x, precision);

        // Insert the result into the database
        String insertQuery = "INSERT INTO calculations (x_value, result) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setBigDecimal(1, x);
            insertStmt.setBigDecimal(2, result);
            insertStmt.executeUpdate();
        }

        // Retrieve the result from the database
        String selectQuery = "SELECT result FROM calculations WHERE x_value = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setBigDecimal(1, x);
            ResultSet resultSet = selectStmt.executeQuery();
            assertTrue(resultSet.next());
            assertEquals(result, resultSet.getBigDecimal("result").setScale(precision.intValue(), RoundingMode.FLOOR));
        }
    }

    /**
     * Tests bulk insertion and retrieval of calculation results.
     */
    @Test
    void testBulkInsertAndRetrieve() throws Exception {
        FunctionsSystem functionsSystem = FunctionsSystem.getInstance();

        // Insert multiple calculation results
        String insertQuery = "INSERT INTO calculations (x_value, result) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            for (int i = 1; i <= 10; i++) {
                BigDecimal x = new BigDecimal(i);
                BigDecimal precision = new BigDecimal("0.0000000001");
                BigDecimal result = functionsSystem.calculate(x, precision);

                insertStmt.setBigDecimal(1, x);
                insertStmt.setBigDecimal(2, result);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }

        // Retrieve and verify all results
        String selectQuery = "SELECT x_value, result FROM calculations ORDER BY x_value";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = selectStmt.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count++;
                BigDecimal x = resultSet.getBigDecimal("x_value");
                BigDecimal result = resultSet.getBigDecimal("result");

                assertNotNull(x);
                assertNotNull(result);
            }
            assertEquals(10, count);
        }
    }

    /**
     * Tests the database schema to ensure it contains the expected tables and columns.
     */
    @Test
    void testDatabaseSchema() throws Exception {
        // Verify that the 'calculations' table exists
        String checkTableQuery = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'calculations')";
        try (PreparedStatement checkTableStmt = connection.prepareStatement(checkTableQuery)) {
            ResultSet resultSet = checkTableStmt.executeQuery();
            assertTrue(resultSet.next());
            assertTrue(resultSet.getBoolean(1));
        }

        // Verify that the 'calculations' table has the expected columns
        String checkColumnsQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = 'calculations'";
        try (PreparedStatement checkColumnsStmt = connection.prepareStatement(checkColumnsQuery)) {
            ResultSet resultSet = checkColumnsStmt.executeQuery();
            Set<String> columns = new HashSet<>();
            while (resultSet.next()) {
                columns.add(resultSet.getString("column_name"));
            }
            assertTrue(columns.contains("x_value"));
            assertTrue(columns.contains("result"));
        }
    }

    /**
     * Tests backward compatibility of migrations by manually rolling back the schema to a previous version
     * and verifying that the database remains functional.
     */
    @Test
    void testBackwardCompatibility() throws Exception {
        // Get the current version of the schema
        String currentVersion = flyway.info().current().getVersion().toString();
        log.info("Current schema version: {}", currentVersion);

        // Get the list of applied migrations
        MigrationInfo[] appliedMigrations = flyway.info().applied();
        for (MigrationInfo migration : appliedMigrations) {
            log.info("Applied migration: {} (State: {})", migration.getVersion(), migration.getState());
        }

        // Manually roll back to the previous version
        // Step 1: Clean the database (removes all objects)
        flyway.clean();

        // Step 2: Migrate to the previous version
        // For example, if the current version is "3", migrate to "2"
        String previousVersion = "2"; // Replace with the desired version
        flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .target(previousVersion) // Target the previous version
                .load();
        flyway.migrate();

        // Verify that the schema is rolled back
        String rolledBackVersion = flyway.info().current().getVersion().toString();
        log.info("Rolled back schema version: {}", rolledBackVersion);
        assertEquals(previousVersion, rolledBackVersion);

        // Verify that the database remains functional
        testCrudOperations();

        // Reapply the latest migrations
        flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .load();
        flyway.migrate();

        // Verify that the schema is restored to the latest version
        String restoredVersion = flyway.info().current().getVersion().toString();
        log.info("Restored schema version: {}", restoredVersion);
        assertEquals(currentVersion, restoredVersion);
    }
}