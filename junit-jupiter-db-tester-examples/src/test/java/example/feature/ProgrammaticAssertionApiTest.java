package example.feature;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import io.github.seijikohara.dbtester.extension.DatabaseTestExtension;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import java.nio.file.Path;
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
 * Demonstrates programmatic assertion API using {@link DatabaseBridge}.
 *
 * <p>This test demonstrates direct usage of {@link DatabaseBridge} assertion methods without
 * relying on {@code @Expectation} annotation. Key programmatic API features:
 *
 * <ul>
 *   <li>{@link DatabaseBridge#loadCsvDataSet(Path)} - Load datasets programmatically
 *   <li>{@link DatabaseBridge#assertEqualsByQuery} - Compare expected data against SQL query
 *       results
 *   <li>{@link DatabaseBridge#assertEquals} - Compare two datasets directly
 *   <li>{@link DatabaseBridge#assertEqualsIgnoreColumns} - Compare datasets ignoring specific
 *       columns
 * </ul>
 *
 * <p>Programmatic assertions provide flexibility for complex validation scenarios where
 * annotation-based testing is insufficient, such as custom SQL queries, dynamic column filtering,
 * or comparing multiple dataset sources.
 */
@ExtendWith(DatabaseTestExtension.class)
public final class ProgrammaticAssertionApiTest {

  /** Logger instance. */
  private static final Logger logger = LoggerFactory.getLogger(ProgrammaticAssertionApiTest.class);

  /** Test database connection. */
  private static DataSource dataSource;

  /** Creates ProgrammaticAssertionApiTest instance. */
  public ProgrammaticAssertionApiTest() {}

  /**
   * Sets up H2 in-memory database and schema.
   *
   * @param context extension context
   * @throws Exception if setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up database for ProgrammaticAssertionApiTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/ProgrammaticAssertionApiTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:ProgrammaticAssertionApiTest;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  /**
   * Executes SQL script from classpath.
   *
   * @param dataSource target DataSource
   * @param scriptPath classpath resource path
   * @throws Exception if execution fails
   */
  private static void executeScript(final DataSource dataSource, final String scriptPath)
      throws Exception {
    final var resource =
        Optional.ofNullable(
                ProgrammaticAssertionApiTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates basic programmatic assertion without annotations.
   *
   * <p>Shows direct use of {@link DatabaseBridge} assertion APIs for custom validation scenarios
   * where annotation-based testing is insufficient.
   *
   * @throws Exception if test fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldDemonstrateBasicProgrammaticAPI() throws Exception {
    logger.info("Running programmatic API demonstration");

    // Execute business logic
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3) VALUES (3, 'Value3', 300, NULL)");
    }

    logger.info("Programmatic API demonstration completed - uses standard @Expectation validation");
  }

  /**
   * Demonstrates programmatic custom SQL query validation.
   *
   * <p>This test shows advanced usage of custom SQL queries for targeted validation beyond simple
   * table comparisons.
   *
   * @throws Exception if test fails
   */
  @Test
  @Preparation
  void shouldValidateUsingMultipleQueries() throws Exception {
    logger.info("Running multiple query validation test");

    // Insert additional test data
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2) VALUES (3, 'Value3', 300)");
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2) VALUES (4, 'Value4', 400)");
    }

    logger.info("Multiple query validation completed");
  }
}
