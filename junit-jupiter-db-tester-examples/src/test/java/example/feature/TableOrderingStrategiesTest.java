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
 * Demonstrates table ordering strategies for foreign key constraints.
 *
 * <p>This test shows:
 *
 * <ul>
 *   <li>Automatic alphabetical table ordering (default)
 *   <li>Manual ordering via {@code table-ordering.txt} file
 *   <li>Programmatic ordering via {@code table-ordering.txt} in custom directories
 *   <li>Handling foreign key constraints
 *   <li>Complex table dependencies (many-to-many relationships)
 * </ul>
 *
 * <p>Table ordering is critical when:
 *
 * <ul>
 *   <li>Tables have foreign key relationships
 *   <li>Parent tables must be loaded before child tables
 *   <li>Junction tables require both parent tables
 *   <li>Deletion order must be reverse of insertion order
 * </ul>
 *
 * <p>Schema:
 *
 * <pre>
 * TABLE1 (parent)
 *   ↓
 * TABLE2 (child of TABLE1)
 *   ↓
 * TABLE3 (independent)
 *   ↓
 * TABLE4 (junction: TABLE2 + TABLE3)
 * </pre>
 */
@ExtendWith(DatabaseTestExtension.class)
public final class TableOrderingStrategiesTest {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(TableOrderingStrategiesTest.class);

  /** DataSource for test database operations. */
  private static DataSource dataSource;

  /** Creates TableOrderingStrategiesTest instance. */
  public TableOrderingStrategiesTest() {}

  /**
   * Sets up H2 in-memory database connection and schema.
   *
   * @param context the extension context
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setupDatabase(final ExtensionContext context) throws Exception {
    logger.info("Setting up H2 in-memory database for TableOrderingStrategiesTest");

    final var testRegistry = DatabaseTestExtension.getRegistry(context);
    dataSource = createDataSource();
    testRegistry.registerDefault(dataSource);
    executeScript(dataSource, "ddl/feature/TableOrderingStrategiesTest.sql");

    logger.info("Database setup completed");
  }

  /**
   * Creates an H2 in-memory DataSource.
   *
   * @return configured DataSource
   */
  private static DataSource createDataSource() {
    final var dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:TableOrderingStrategiesTest;DB_CLOSE_DELAY=-1");
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
                TableOrderingStrategiesTest.class.getClassLoader().getResource(scriptPath))
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
   * Demonstrates automatic alphabetical table ordering.
   *
   * <p>Framework orders tables alphabetically: TABLE1, TABLE2, TABLE3, TABLE4. This works well when
   * foreign keys follow alphabetical order.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldUseAlphabeticalOrdering() throws Exception {
    logger.info("Running alphabetical ordering test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("INSERT INTO TABLE1 (ID, COLUMN1) VALUES (3, 'Services')");
      statement.executeUpdate(
          "INSERT INTO TABLE2 (ID, COLUMN1, COLUMN2) VALUES (4, 3, 'Consulting')");
    }

    logger.info("Alphabetical ordering test completed");
  }

  /**
   * Demonstrates manual table ordering via table-ordering.txt file.
   *
   * <p>Uses {@code table-ordering.txt} to specify correct insertion order for foreign keys.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldUseManualOrdering() throws Exception {
    logger.info("Running manual ordering test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("INSERT INTO TABLE3 (ID, COLUMN1) VALUES (4, 'Featured')");
      statement.executeUpdate("INSERT INTO TABLE4 (COLUMN1, COLUMN2) VALUES (1, 4)");
    }

    logger.info("Manual ordering test completed");
  }

  /**
   * Demonstrates custom resource location for table ordering.
   *
   * <p>Uses {@code table-ordering.txt} in a custom directory to explicitly control table insertion
   * order.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/TableOrderingStrategiesTest/programmatic/"))
  @Expectation(
      dataSets =
          @DataSet(
              resourceLocation =
                  "classpath:example/feature/TableOrderingStrategiesTest/programmatic/expected/"))
  void shouldUseProgrammaticOrdering() throws Exception {
    logger.info("Running programmatic ordering test");

    // Execute business logic
    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE2 SET COLUMN2 = 'Updated Widget' WHERE ID = 1");
    }

    logger.info("Programmatic ordering test completed");
  }

  /**
   * Demonstrates handling complex many-to-many relationships.
   *
   * <p>Shows proper ordering for junction tables with multiple foreign keys.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation
  @Expectation
  void shouldHandleManyToManyRelationships() throws Exception {
    logger.info("Running many-to-many relationships test");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      // Add new records to parent tables
      statement.executeUpdate("INSERT INTO TABLE1 (ID, COLUMN1) VALUES (4, 'Accessories')");
      statement.executeUpdate("INSERT INTO TABLE2 (ID, COLUMN1, COLUMN2) VALUES (5, 4, 'Cable')");
      // Add new tag
      statement.executeUpdate("INSERT INTO TABLE3 (ID, COLUMN1) VALUES (5, 'Essential')");
      // Create associations
      statement.executeUpdate("INSERT INTO TABLE4 (COLUMN1, COLUMN2) VALUES (5, 1)");
      statement.executeUpdate("INSERT INTO TABLE4 (COLUMN1, COLUMN2) VALUES (5, 5)");
    }

    logger.info("Many-to-many relationships test completed");
  }
}
