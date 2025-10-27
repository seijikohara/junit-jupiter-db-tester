package example.feature;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.seijikohara.dbtester.api.annotation.DataSet;
import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
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
 * Demonstrates partial column validation techniques using CSV files.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Validating only specific columns via partial CSV files
 *   <li>Excluding auto-generated columns (ID, timestamps) from CSV expectations
 *   <li>Testing business logic without worrying about database-generated values
 *   <li>Using custom expectation paths for different validation scenarios
 * </ul>
 *
 * <p>Use partial column validation when:
 *
 * <ul>
 *   <li>Testing tables with auto-increment IDs
 *   <li>Ignoring timestamp columns (CREATED_AT, UPDATED_AT)
 *   <li>Focusing on business-relevant columns only
 *   <li>Dealing with database-generated values (UUIDs, sequences)
 * </ul>
 *
 * <p>Note: For programmatic column exclusion using {@code
 * DatabaseAssertion.assertEqualsIgnoreColumns}, you would need to manually create datasets using
 * DbUnit APIs.
 */
@ExtendWith(DatabaseTestExtension.class)
public final class PartialColumnValidationTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(PartialColumnValidationTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates PartialColumnValidationTest instance. */
  public PartialColumnValidationTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for PartialColumnValidationTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/PartialColumnValidationTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:PartialColumnValidationTest;DB_CLOSE_DELAY=-1");
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
        Optional.ofNullable(
                PartialColumnValidationTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates partial column validation using CSV with subset of columns.
   *
   * <p>CSV contains only business-relevant columns, ignoring auto-generated ID and timestamp.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldValidatePartialColumnsViaCSV() throws Exception {
    logger.info("Running partial column validation via CSV test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (COLUMN1, COLUMN2, COLUMN3)
              VALUES ('DELETE', 'User', 789)
              """);
    }

    logger.info("Partial column validation via CSV completed");
  }

  /**
   * Demonstrates validation with partial CSV (ignoring auto-generated columns).
   *
   * <p>CSV file contains only business columns, excluding ID, COLUMN4, and COLUMN5.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/PartialColumnValidationTest/expected-ignore-columns/"))
  void shouldIgnoreAutoGeneratedColumns() throws Exception {
    logger.info("Running ignore auto-generated columns test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (COLUMN1, COLUMN2, COLUMN3)
              VALUES ('UPDATE', 'Product', 456)
              """);
    }

    logger.info("Ignore auto-generated columns test completed");
  }

  /**
   * Demonstrates validation with minimal CSV columns.
   *
   * <p>CSV contains only the most essential business columns for validation.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/PartialColumnValidationTest/expected-combined/"))
  void shouldValidateWithMinimalColumns() throws Exception {
    logger.info("Running minimal columns validation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (COLUMN1, COLUMN2, COLUMN3)
              VALUES ('CREATE', 'Order', 999)
              """);
    }

    logger.info("Minimal columns validation test completed");
  }

  /**
   * Demonstrates validation after UPDATE operation.
   *
   * <p>Note: This test validates the complete table state after an update operation. True partial
   * column validation (validating only specific columns while ignoring others) requires
   * programmatic assertions using {@code DatabaseAssertion.assertEqualsIgnoreColumns}, which is
   * beyond the scope of annotation-based testing.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/PartialColumnValidationTest/expected-after-update/"))
  void shouldValidateAfterUpdate() throws Exception {
    logger.info("Running validate after update test");

    // Simulate update operation
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE1 SET COLUMN3 = 555 WHERE ID = 1");
    }

    logger.info("Validate after update test completed");
  }
}
