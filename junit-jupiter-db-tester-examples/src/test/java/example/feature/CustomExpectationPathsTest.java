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
 * Demonstrates custom expectation paths for flexible test data organization.
 *
 * <p>This test demonstrates using {@link DataSet#resourceLocation()} to specify custom paths for
 * expectation data, enabling flexible test data organization beyond convention-based defaults.
 *
 * <p>Key features demonstrated:
 *
 * <ul>
 *   <li>Custom expectation paths using {@link DataSet} annotation
 *   <li>Organizing multiple expectation scenarios in subdirectories
 *   <li>Multi-stage testing with different expected states
 *   <li>Complex business logic validation with database state changes
 * </ul>
 *
 * <p>This approach is useful when tests require multiple expectation variants or when
 * convention-based paths are insufficient for complex test scenarios.
 */
@ExtendWith(DatabaseTestExtension.class)
public final class CustomExpectationPathsTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(CustomExpectationPathsTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates CustomExpectationPathsTest instance. */
  public CustomExpectationPathsTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for CustomExpectationPathsTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/CustomExpectationPathsTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:CustomExpectationPathsTest;DB_CLOSE_DELAY=-1");
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
                CustomExpectationPathsTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates basic database testing with INSERT operation.
   *
   * <p>Uses custom expectation path to validate database state after inserting new record.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/CustomExpectationPathsTest/expected-basic/"))
  void shouldInsertNewOrder() throws Exception {
    logger.info("Running basic INSERT test");

    // Execute business logic
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3, COLUMN4)
              VALUES (2, 1, 299.99, '2024-02-15', 'PENDING')
              """);
    }

    logger.info("Basic INSERT test completed");
  }

  /**
   * Demonstrates validating database state with custom expectation path.
   *
   * <p>Note: Column ignoring would typically be done using partial CSV files (excluding columns you
   * don't want to validate) rather than programmatic assertion.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/CustomExpectationPathsTest/expected-ignore-columns/"))
  void shouldValidateWithPartialColumns() throws Exception {
    logger.info("Running partial column validation test");

    // Execute business logic
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3, COLUMN4)
              VALUES (2, 2, 599.99, '2024-03-20', 'SHIPPED')
              """);
    }

    logger.info("Partial column validation test completed");
  }

  /**
   * Demonstrates validation with custom expectation path.
   *
   * <p>Note: Custom query validation using {@code DatabaseAssertion.assertEqualsByQuery} requires
   * programmatic setup beyond annotation-based testing.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/CustomExpectationPathsTest/expected-query/"))
  void shouldValidateOrderItems() throws Exception {
    logger.info("Running order items validation test");

    // Execute business logic: Add related items
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE2 (ID, COLUMN1, COLUMN2, COLUMN3, COLUMN4)
              VALUES (3, 1, 'Headphones', 1, 79.99)
              """);
    }

    logger.info("Order items validation completed");
  }

  /**
   * Demonstrates complex workflow (stage 1).
   *
   * <p>Creates a new record and validates the initial state.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/CustomExpectationPathsTest/expected-stage1/"))
  void shouldCreateOrder() throws Exception {
    logger.info("Running order creation test (stage 1)");

    // Stage 1: Create record
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3, COLUMN4)
              VALUES (2, 1, 150.00, '2024-04-10', 'PENDING')
              """);
    }

    logger.info("Order creation test completed");
  }

  /**
   * Demonstrates status update workflow (stage 2).
   *
   * <p>Creates a record and then updates its status.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/CustomExpectationPathsTest/expected-stage2/"))
  void shouldShipOrder() throws Exception {
    logger.info("Running order shipment test (stage 2)");

    // Create record
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3, COLUMN4)
              VALUES (2, 1, 150.00, '2024-04-10', 'PENDING')
              """);
    }

    // Update status
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE1 SET COLUMN4 = 'SHIPPED' WHERE ID = 2");
    }

    logger.info("Order shipment test completed");
  }
}
