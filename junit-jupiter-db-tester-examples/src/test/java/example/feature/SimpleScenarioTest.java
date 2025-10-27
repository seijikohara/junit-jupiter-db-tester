package example.feature;

import static java.nio.charset.StandardCharsets.UTF_8;

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
 * Demonstrates scenario-based testing with CSV filtering.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Using scenario marker column for row filtering
 *   <li>Sharing a single CSV file across multiple test methods
 *   <li>Test method name as automatic scenario filter
 *   <li>Reducing CSV file duplication
 * </ul>
 *
 * <p>CSV files contain scenario marker column that filters rows by test method name:
 *
 * <pre>
 * [Scenario],ID,COLUMN1,COLUMN2,COLUMN3
 * shouldCreateActiveUser,1,alice,alice@example.com,ACTIVE
 * shouldCreateInactiveUser,1,bob,bob@example.com,INACTIVE
 * </pre>
 */
@ExtendWith(DatabaseTestExtension.class)
public final class SimpleScenarioTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(SimpleScenarioTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates SimpleScenarioTest instance. */
  public SimpleScenarioTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for SimpleScenarioTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/SimpleScenarioTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:SimpleScenarioTest;DB_CLOSE_DELAY=-1");
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
        Optional.ofNullable(SimpleScenarioTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates scenario filtering for active user creation.
   *
   * <p>Only rows matching the test method name are loaded from TABLE1.csv.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldCreateActiveUser() throws Exception {
    logger.info("Running scenario test: shouldCreateActiveUser");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (2, 'charlie', 'charlie@example.com', 'ACTIVE')
              """);
    }

    logger.info("Active user created successfully");
  }

  /**
   * Demonstrates scenario filtering for inactive user creation.
   *
   * <p>Only rows matching the test method name are loaded from TABLE1.csv.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldCreateInactiveUser() throws Exception {
    logger.info("Running scenario test: shouldCreateInactiveUser");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (2, 'david', 'david@example.com', 'INACTIVE')
              """);
    }

    logger.info("Inactive user created successfully");
  }

  /**
   * Demonstrates scenario filtering with multiple existing users.
   *
   * <p>Only rows matching the test method name are loaded from TABLE1.csv.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldHandleMultipleUsers() throws Exception {
    logger.info("Running scenario test: shouldHandleMultipleUsers");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      // Update user status
      statement.executeUpdate("UPDATE TABLE1 SET COLUMN3 = 'SUSPENDED' WHERE ID = 2");
    }

    logger.info("User status updated successfully");
  }
}
