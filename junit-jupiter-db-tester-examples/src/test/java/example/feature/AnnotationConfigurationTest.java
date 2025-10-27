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
 * Demonstrates advanced annotation configuration features.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Explicit {@code resourceLocation} specification
 *   <li>Multiple {@code scenarioNames} in a single DataSet
 *   <li>Class-level vs method-level annotation precedence
 *   <li>Custom directory structure
 *   <li>Multiple tables with foreign key relationships
 * </ul>
 *
 * <p>Directory structure:
 *
 * <pre>
 * example/feature/AnnotationConfigurationTest/
 *   custom-location/
 *     TABLE1.csv
 *     TABLE2.csv
 *   expected/
 *     TABLE1.csv
 *     TABLE2.csv
 * </pre>
 */
@ExtendWith(DatabaseTestExtension.class)
@Preparation(
    dataSets =
        @DataSet(
            resourceLocation = "classpath:example/feature/AnnotationConfigurationTest/",
            scenarioNames = "classLevel"))
public final class AnnotationConfigurationTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigurationTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates AnnotationConfigurationTest instance. */
  public AnnotationConfigurationTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for AnnotationConfigurationTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/AnnotationConfigurationTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:AnnotationConfigurationTest;DB_CLOSE_DELAY=-1");
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
                AnnotationConfigurationTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates explicit resource location specification.
   *
   * <p>Uses custom directory path instead of convention-based resolution.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/AnnotationConfigurationTest/custom-location/"))
  @Expectation
  void shouldUseCustomResourceLocation() throws Exception {
    logger.info("Running custom resource location test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2) VALUES (3, 'Marketing', 'Tokyo')");
    }

    logger.info("Custom resource location test completed");
  }

  /**
   * Demonstrates multiple scenario names in a single test.
   *
   * <p>Loads rows matching either scenario name from the same CSV files.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(dataSets = @DataSet(scenarioNames = {"scenario1", "scenario2"}))
  @Expectation
  void shouldHandleMultipleScenarios() throws Exception {
    logger.info("Running multiple scenarios test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE2 SET COLUMN3 = 65000.00 WHERE ID = 2");
    }

    logger.info("Multiple scenarios test completed");
  }

  /**
   * Demonstrates multiple {@code @DataSet} annotations with data merging.
   *
   * <p>Uses multiple {@code @DataSet} in both preparation and expectation to show how data from
   * different scenarios is merged together. Each DataSet loads different rows using scenario names,
   * and the framework combines them.
   *
   * <p>This should produce the same result as using a single {@code @DataSet} with multiple
   * scenario names.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(
      dataSets = {@DataSet(scenarioNames = "scenario1"), @DataSet(scenarioNames = "scenario2")})
  @Expectation(
      dataSets = {@DataSet(scenarioNames = "scenario1"), @DataSet(scenarioNames = "scenario2")})
  void shouldMergeMultipleDataSets() throws Exception {
    logger.info("Running multiple DataSet array test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE2 SET COLUMN3 = 65000.00 WHERE ID = 2");
    }

    logger.info("Multiple DataSet array test completed");
  }

  /**
   * Demonstrates class-level annotation inheritance.
   *
   * <p>This test uses the class-level {@code @Preparation} annotation defined at the class level.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Expectation
  void shouldUseClassLevelAnnotation() throws Exception {
    logger.info("Running class-level annotation test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
              INSERT INTO TABLE2 (ID, COLUMN1, COLUMN2, COLUMN3)
              VALUES (100, 'New Employee', 1, 45000.00)
              """);
    }

    logger.info("Class-level annotation test completed");
  }

  /**
   * Demonstrates using multiple {@code @DataSet} annotations in a single test.
   *
   * <p>This test shows how to use multiple {@code scenarioNames} within a single DataSet to load
   * data from different scenarios in the same CSV files.
   *
   * <p>Multiple scenarios are combined: {@code multiDataSet1} loads departments and one employee,
   * while the test adds one more department via INSERT.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(dataSets = @DataSet(scenarioNames = "multiDataSet1"))
  @Expectation(dataSets = @DataSet(scenarioNames = "multiDataSet"))
  void shouldHandleMultipleDataSets() throws Exception {
    logger.info("Running multiple DataSets test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
          INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2)
          VALUES (4, 'Research', 'Osaka')
          """);
    }

    logger.info("Multiple DataSets test completed");
  }
}
