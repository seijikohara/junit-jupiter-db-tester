package example.feature;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import io.github.seijikohara.dbtester.api.operation.Operation;
import io.github.seijikohara.dbtester.extension.DatabaseTestExtension;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates all database operations for test data preparation.
 *
 * <p>This test provides comprehensive coverage of all {@link Operation} enum values:
 *
 * <ul>
 *   <li>{@link Operation#CLEAN_INSERT} - Delete all rows, then insert (default, most common)
 *   <li>{@link Operation#INSERT} - Insert new rows (fails if primary key already exists)
 *   <li>{@link Operation#UPDATE} - Update existing rows only (fails if row not exists)
 *   <li>{@link Operation#REFRESH} - Update if exists, insert if not (upsert)
 *   <li>{@link Operation#DELETE} - Delete only specified rows by primary key
 *   <li>{@link Operation#DELETE_ALL} - Delete all rows from tables
 *   <li>{@link Operation#TRUNCATE_TABLE} - Truncate tables, resetting auto-increment sequences
 *   <li>{@link Operation#TRUNCATE_INSERT} - Truncate then insert (predictable IDs)
 * </ul>
 *
 * <p>Each operation demonstrates different data manipulation strategies for test scenarios.
 *
 * <p><strong>Note on Partial Column Validation:</strong> The expectation CSV files in this test
 * omit COLUMN3 (TIMESTAMP) to demonstrate that you don't need to specify all table columns in CSV
 * files. Only columns relevant to your test assertions need to be included. This is particularly
 * useful for excluding columns with dynamic values (timestamps, auto-generated IDs) or columns not
 * relevant to the specific test case.
 */
@ExtendWith(DatabaseTestExtension.class)
public final class OperationVariationsTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(OperationVariationsTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates OperationVariationsTest instance. */
  public OperationVariationsTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for OperationVariationsTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/OperationVariationsTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:OperationVariationsTest;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  /**
   * Executes a SQL script from classpath.
   *
   * @param dataSource the DataSource to execute against
   * @param scriptPath the classpath resource path
   * @throws Exception if script execution fails
   */
  private static void executeScript(final DataSource dataSource, final String scriptPath)
      throws Exception {
    final var resource =
        Optional.ofNullable(OperationVariationsTest.class.getClassLoader().getResource(scriptPath))
            .orElseThrow(
                () -> new IllegalStateException(String.format("Script not found: %s", scriptPath)));

    try (final var connection = dataSource.getConnection();
        final var statement = connection.createStatement();
        final var inputStream = resource.openStream()) {
      final var sql = new String(inputStream.readAllBytes(), UTF_8);
      Arrays.stream(sql.split(";"))
          .map(String::trim)
          .filter(trimmed -> !trimmed.isEmpty())
          .forEach(
              trimmed -> {
                try {
                  statement.execute(trimmed);
                } catch (final SQLException e) {
                  throw new RuntimeException(
                      String.format("Failed to execute SQL: %s", trimmed), e);
                }
              });
    }
  }

  /**
   * Demonstrates CLEAN_INSERT operation (default).
   *
   * <p>Deletes all existing rows, then inserts test data. Most common operation for test setup.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.CLEAN_INSERT)
  @Expectation
  void shouldUseCleanInsertOperation() throws Exception {
    logger.info("Running CLEAN_INSERT operation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (3, 'Tablet', 25, CURRENT_TIMESTAMP)
              """);
    }

    logger.info("CLEAN_INSERT operation completed");
  }

  /**
   * Demonstrates INSERT operation.
   *
   * <p>Inserts new rows without deleting existing data. Fails if row with same ID already exists.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.INSERT)
  @Expectation
  void shouldUseInsertOperation() throws Exception {
    logger.info("Running INSERT operation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (3, 'Smartwatch', 30, CURRENT_TIMESTAMP)
              """);
    }

    logger.info("INSERT operation completed");
  }

  /**
   * Demonstrates UPDATE operation.
   *
   * <p>Updates existing rows only. Fails if row with specified ID does not exist.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.UPDATE)
  @Expectation
  void shouldUseUpdateOperation() throws Exception {
    logger.info("Running UPDATE operation test");

    // First insert a row to update later
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (1, 'Laptop', 5, CURRENT_TIMESTAMP)
              """);
      statement.executeUpdate("UPDATE TABLE1 SET COLUMN2 = 8 WHERE ID = 2");
    }

    logger.info("UPDATE operation completed");
  }

  /**
   * Demonstrates REFRESH operation (upsert).
   *
   * <p>Updates row if exists, inserts if not exists. Flexible operation for mixed scenarios.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.REFRESH)
  @Expectation
  void shouldUseRefreshOperation() throws Exception {
    logger.info("Running REFRESH operation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (3, 'Headphones', 40, CURRENT_TIMESTAMP)
              """);
    }

    logger.info("REFRESH operation completed");
  }

  /**
   * Demonstrates DELETE_ALL followed by INSERT.
   *
   * <p>Clears table completely before inserting test data.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.DELETE_ALL)
  @Expectation
  void shouldUseDeleteAllOperation() throws Exception {
    logger.info("Running DELETE_ALL operation test");

    // After DELETE_ALL, table is empty
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (1, 'Camera', 15, CURRENT_TIMESTAMP)
              """);
    }

    logger.info("DELETE_ALL operation completed");
  }

  /**
   * Demonstrates testing deletion scenarios with database validation.
   *
   * <p>This test demonstrates validating database state after delete operations. While {@link
   * Operation#DELETE} exists for removing specific test data during preparation, the most common
   * testing pattern is:
   *
   * <ol>
   *   <li>Insert initial data (rows 1, 2, 3) using CLEAN_INSERT in @Preparation
   *   <li>Execute application logic that performs deletion (row 2)
   *   <li>Verify the final state with @Expectation (rows 1 and 3 remain)
   * </ol>
   *
   * <p>This pattern tests the actual business logic that performs deletion, rather than just the
   * database operation itself.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldUseDeleteOperation() throws Exception {
    logger.info("Running DELETE operation test");

    // Execute business logic that deletes specific rows
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      // Simulate application code deleting row 2
      statement.executeUpdate("DELETE FROM TABLE1 WHERE ID = 2");
    }

    logger.info("DELETE operation test completed");
  }

  /**
   * Demonstrates TRUNCATE_INSERT operation.
   *
   * <p>Truncates tables (removing all data and resetting sequences), then inserts test data. Use
   * this when auto-increment sequences must be reset for predictable ID values.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.TRUNCATE_INSERT)
  @Expectation
  void shouldUseTruncateInsertOperation() throws Exception {
    logger.info("Running TRUNCATE_INSERT operation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (3, 'Monitor', 12, NULL)
              """);
    }

    logger.info("TRUNCATE_INSERT operation completed");
  }

  /**
   * Demonstrates TRUNCATE_TABLE operation.
   *
   * <p>Truncates tables, removing all data and resetting auto-increment sequences. Faster than
   * DELETE_ALL but may not be supported by all databases.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(operation = Operation.TRUNCATE_TABLE)
  @Expectation
  void shouldUseTruncateTableOperation() throws Exception {
    logger.info("Running TRUNCATE_TABLE operation test");

    // After TRUNCATE_TABLE, table is empty and sequences are reset
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (1, 'Keyboard', 25, NULL)
              """);
    }

    logger.info("TRUNCATE_TABLE operation completed");
  }
}
