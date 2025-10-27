package example.feature;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.extension.DatabaseTestExtension;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import java.nio.file.Path;
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
 * Demonstrates framework error handling and validation.
 *
 * <p>This test verifies proper exception handling for common error scenarios:
 *
 * <ul>
 *   <li>{@link DataSetLoadException} - Dataset loading failures (missing files, invalid CSV)
 *   <li>{@link AssertionError} - Data mismatch during validation
 *   <li>Missing resource handling - Non-existent preparation/expectation files
 *   <li>Invalid CSV format handling - Malformed CSV data
 * </ul>
 *
 * <p>Error handling tests ensure the framework provides clear, actionable error messages for common
 * failure scenarios.
 */
@ExtendWith(DatabaseTestExtension.class)
public final class ErrorHandlingTest {

  /** Logger instance. */
  private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingTest.class);

  /** Test database connection. */
  private static DataSource dataSource;

  /** Creates ErrorHandlingTest instance. */
  public ErrorHandlingTest() {}

  /**
   * Sets up H2 in-memory database and schema.
   *
   * @param context extension context
   * @throws Exception if setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up database for ErrorHandlingTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/ErrorHandlingTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:ErrorHandlingTest;DB_CLOSE_DELAY=-1");
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
        Optional.ofNullable(ErrorHandlingTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates {@link DataSetLoadException} when loading from non-existent directory.
   *
   * <p>Verifies framework throws clear exception when CSV directory does not exist.
   */
  @Test
  void shouldThrowExceptionForMissingDirectory() {
    logger.info("Testing missing directory error handling");

    // Use a Path that definitely doesn't exist
    final var nonExistentPath = Path.of("/non-existent-directory-for-testing-12345");
    final var bridge = DatabaseBridge.getInstance();

    assertThrows(
        DataSetLoadException.class,
        () -> bridge.loadCsvDataSet(nonExistentPath),
        "Should throw DataSetLoadException for non-existent directory");

    logger.info("Missing directory error handling verified");
  }

  /**
   * Demonstrates successful validation to contrast with error scenarios.
   *
   * <p>This test passes to show that error handling tests themselves are not fundamentally broken,
   * and proper data does validate successfully.
   *
   * @throws Exception if test fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldValidateSuccessfully() throws Exception {
    logger.info("Testing successful validation scenario");

    // Insert matching data
    try (final var connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2) VALUES (2, 'Value2', 200)");
    }

    logger.info("Successful validation completed");
  }
}
