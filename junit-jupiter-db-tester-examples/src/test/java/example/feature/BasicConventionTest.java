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
 * Demonstrates the simplest convention-based database testing.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Automatic CSV file resolution based on test class and method names
 *   <li>Default {@code @Preparation} and {@code @Expectation} usage
 *   <li>Single table operations with minimal configuration
 *   <li>H2 in-memory database setup
 * </ul>
 *
 * <p>CSV files are located at:
 *
 * <ul>
 *   <li>{@code src/test/resources/example/feature/BasicConventionTest/PRODUCTS.csv}
 *   <li>{@code src/test/resources/example/feature/BasicConventionTest/expected/PRODUCTS.csv}
 * </ul>
 */
@ExtendWith(DatabaseTestExtension.class)
public final class BasicConventionTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(BasicConventionTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates BasicConventionTest instance. */
  public BasicConventionTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for BasicConventionTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/BasicConventionTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:BasicConventionTest;DB_CLOSE_DELAY=-1");
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
        Optional.ofNullable(BasicConventionTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates the simplest convention-based test.
   *
   * <p>This test:
   *
   * <ul>
   *   <li>Loads preparation data from {@code PRODUCTS.csv}
   *   <li>Executes business logic (simulated by direct SQL)
   *   <li>Validates final state against {@code expected/PRODUCTS.csv}
   * </ul>
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldLoadAndVerifyProductData() throws Exception {
    logger.info("Running basic convention test");

    // Simulate business logic: Add a new product
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2) VALUES (3, 'Keyboard', 79.99)");
    }

    logger.info("Product data inserted successfully");
  }
}
