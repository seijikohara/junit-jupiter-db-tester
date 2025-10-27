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
 * Demonstrates using multiple named data sources in a single test.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Registering multiple named data sources
 *   <li>Using {@code dataSourceName} in {@code @DataSet} annotations
 *   <li>Working with different databases simultaneously
 * </ul>
 *
 * <p>Use cases:
 *
 * <ul>
 *   <li>Multi-tenant applications with separate database instances
 *   <li>Microservices with their own databases
 *   <li>Testing data synchronization between databases
 * </ul>
 */
@ExtendWith(DatabaseTestExtension.class)
public final class MultipleDataSourceTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(MultipleDataSourceTest.class);

  /** Primary database DataSource. */
  private static DataSource primaryDataSource;

  /** Secondary database DataSource. */
  private static DataSource secondaryDataSource;

  /** Creates MultipleDataSourceTest instance. */
  public MultipleDataSourceTest() {}

  /**
   * Sets up two H2 in-memory databases.
   *
   * <p>Creates:
   *
   * <ul>
   *   <li>Default database - primary data store
   *   <li>Named database "inventory" - secondary data store
   * </ul>
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabases(final ExtensionContext context) throws Exception {
    logger.info("Setting up multiple H2 in-memory databases");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);

    // Setup primary database (default)
    primaryDataSource = createPrimaryDataSource();
    testRegistry.registerDefault(primaryDataSource);
    executeScript(primaryDataSource, "ddl/feature/MultipleDataSourceTest-primary.sql");

    // Setup secondary database (named "inventory")
    secondaryDataSource = createSecondaryDataSource();
    testRegistry.register("inventory", secondaryDataSource);
    executeScript(secondaryDataSource, "ddl/feature/MultipleDataSourceTest-secondary.sql");

    logger.info("All databases setup completed");
  }

  /**
   * Creates the primary DataSource.
   *
   * @return configured primary DataSource
   */
  private static DataSource createPrimaryDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:MultipleDataSourceTest_Primary;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  /**
   * Creates the secondary DataSource.
   *
   * @return configured secondary DataSource
   */
  private static DataSource createSecondaryDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:MultipleDataSourceTest_Secondary;DB_CLOSE_DELAY=-1");
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
        Optional.ofNullable(MultipleDataSourceTest.class.getClassLoader().getResource(scriptPath))
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
   * Tests operations on the default (primary) database.
   *
   * <p>Uses default dataSourceName (empty string refers to the default data source).
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(
      dataSets =
          @DataSet(
              resourceLocation = "classpath:example/feature/MultipleDataSourceTest/default/",
              scenarioNames = "default"))
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/MultipleDataSourceTest/default/expected/",
              scenarioNames = "default"))
  void shouldManageCustomersInDefaultDatabase() throws Exception {
    logger.info("Running test on default database");

    try (final Connection connection = primaryDataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
          INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2)
          VALUES (3, 'Charlie Brown', 'charlie@example.com')
          """);
    }

    logger.info("Default database test completed");
  }

  /**
   * Tests operations on the named secondary (inventory) database.
   *
   * <p>Uses {@code dataSourceName = "inventory"} to specify the secondary database.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(
      dataSets =
          @DataSet(
              dataSourceName = "inventory",
              resourceLocation = "classpath:example/feature/MultipleDataSourceTest/inventory/",
              scenarioNames = "inventory"))
  @Expectation(
      dataSets =
          @DataSet(
              dataSourceName = "inventory",
              resourceLocation =
                  "classpath:example/feature/MultipleDataSourceTest/inventory/expected/",
              scenarioNames = "inventory"))
  void shouldManageProductsInInventoryDatabase() throws Exception {
    logger.info("Running test on inventory database");

    try (final Connection connection = secondaryDataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
          INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2)
          VALUES (3, 'Monitor', 25)
          """);
    }

    logger.info("Inventory database test completed");
  }
}
