package io.github.seijikohara.dbtester.internal.bridge.dbunit;

import io.github.seijikohara.dbtester.api.assertion.AssertionFailureHandler;
import io.github.seijikohara.dbtester.api.operation.Operation;
import io.github.seijikohara.dbtester.exception.DatabaseTesterException;
import io.github.seijikohara.dbtester.exception.ValidationException;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.DatabaseAssert;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.FailureHandlerAdapter;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.format.CsvDataSetReader;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.format.DataSetReader;
import io.github.seijikohara.dbtester.internal.dataset.DataSet;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.SchemaName;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridge providing unified access to DbUnit operations, assertions, and dataset loading.
 *
 * <p>This class implements the <strong>Bridge pattern</strong> to provide a single, simplified
 * entry point to DbUnit functionality. It completely isolates DbUnit types from framework code,
 * ensuring the framework remains DbUnit-independent.
 *
 * <h2>Design Pattern</h2>
 *
 * <p><strong>Bridge Pattern:</strong> This class bridges the gap between the framework's domain
 * model and DbUnit's implementation, allowing either side to evolve independently.
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This class serves as the ONLY public interface from the bridge.dbunit package:
 *
 * <pre>
 * Framework Code (internal.junit, api.assertion)
 *       ↓
 * DatabaseBridge (public) ← Single entry point
 *       ↓ delegates to
 * ├── TypeConverter (type conversion)
 * ├── DataSetReader (CSV loading)
 * └── DbUnit API (operations, assertions)
 * </pre>
 *
 * <h2>Core Capabilities</h2>
 *
 * <ul>
 *   <li><strong>Operations:</strong> Execute database operations (INSERT, UPDATE, DELETE, etc.)
 *   <li><strong>Assertions:</strong> Compare datasets and tables with flexible filtering
 *   <li><strong>Loading:</strong> Read CSV datasets from filesystem
 *   <li><strong>Verification:</strong> Validate database state against expected datasets
 * </ul>
 *
 * <h2>Type Isolation</h2>
 *
 * <p>All public methods use only framework types:
 *
 * <ul>
 *   <li>{@code DataSet}, {@code Table}, {@code ScenarioDataSet} (framework domain model)
 *   <li>{@code Operation} (framework enum)
 *   <li>{@code DataSource} (JDBC standard)
 *   <li>{@code Path} (Java NIO)
 * </ul>
 *
 * <p>DbUnit types ({@code IDataSet}, {@code ITable}, {@code DatabaseOperation}) never appear in
 * public method signatures. All conversions happen internally via {@link TypeConverter}.
 *
 * <h2>Usage Pattern</h2>
 *
 * <pre>{@code
 * // Get singleton instance
 * DatabaseBridge bridge = DatabaseBridge.getInstance();
 *
 * // Execute database operation
 * bridge.executeOperation(scenarioDataSet, Operation.CLEAN_INSERT, dataSource);
 *
 * // Load CSV dataset
 * DataSet dataSet = bridge.loadCsvDataSet(Path.of("test-data"));
 *
 * // Verify database state
 * bridge.verifyExpectation(expectedDataSet, dataSource);
 *
 * // Assert dataset equality
 * bridge.assertEquals(expected, actual);
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This bridge is stateless and thread-safe. The singleton instance can be safely shared across
 * multiple threads.
 *
 * <h2>Null Safety</h2>
 *
 * <p>This is a PUBLIC class exposed to internal framework code (not end users). NullAway enforces
 * null safety at compile time; no runtime {@code Objects.requireNonNull()} checks are needed.
 *
 * @see TypeConverter
 * @see DataSetReader
 * @see CsvDataSetReader
 */
public final class DatabaseBridge {

  /** Logger for tracking operations. */
  private static final Logger logger = LoggerFactory.getLogger(DatabaseBridge.class);

  /** Singleton instance. */
  private static final DatabaseBridge INSTANCE = new DatabaseBridge();

  /** CSV dataset reader. */
  private final DataSetReader csvReader;

  /** DbUnit assertion implementation. */
  private final DatabaseAssert databaseAssert;

  /** Private constructor for singleton pattern. */
  private DatabaseBridge() {
    this.csvReader = new CsvDataSetReader();
    this.databaseAssert = new DatabaseAssert();
  }

  /**
   * Gets the singleton instance of DatabaseBridge.
   *
   * @return the singleton instance
   */
  public static DatabaseBridge getInstance() {
    return INSTANCE;
  }

  // ===========================================================================================
  // Database Operations
  // ===========================================================================================

  /**
   * Executes a database operation using the specified dataset.
   *
   * <p>This method performs the complete operation execution workflow:
   *
   * <ol>
   *   <li>Converts framework dataset to DbUnit format
   *   <li>Converts framework operation to DbUnit format
   *   <li>Creates database connection from data source
   *   <li>Executes operation via DbUnit
   *   <li>Closes connection (automatic via try-with-resources)
   * </ol>
   *
   * @param scenarioDataSet the dataset to use for the operation
   * @param operation the operation to execute
   * @param dataSource the target data source
   * @param schemaName the database schema, or null for default
   * @throws DatabaseTesterException if the operation fails
   */
  public void executeOperation(
      final ScenarioDataSet scenarioDataSet,
      final Operation operation,
      final DataSource dataSource,
      final @Nullable SchemaName schemaName) {

    logger.debug(
        "Executing operation {} on {} tables with schema: {}",
        operation,
        scenarioDataSet.getTables().size(),
        Optional.ofNullable(schemaName).map(SchemaName::value).orElse("default"));

    try {
      // Convert framework types to DbUnit types
      final var dbUnitDataSet = TypeConverter.toDbUnitDataSet(scenarioDataSet);
      final var dbUnitOperation = toDbUnitOperation(operation);

      // Execute operation with connection management
      try (final var jdbcConnection = dataSource.getConnection()) {
        final var databaseConnection =
            new DatabaseConnection(
                jdbcConnection,
                Optional.ofNullable(schemaName).map(SchemaName::value).orElse(null));

        dbUnitOperation.execute(databaseConnection, dbUnitDataSet);

        logger.debug(
            "Successfully executed operation {} on {} tables",
            operation,
            scenarioDataSet.getTables().size());
      }
    } catch (final DatabaseUnitException | SQLException e) {
      throw new DatabaseTesterException(
          String.format(
              "Failed to execute operation: %s on schema: %s",
              operation, Optional.ofNullable(schemaName).map(SchemaName::value).orElse("default")),
          e);
    }
  }

  /**
   * Executes a database operation using the connection's default schema.
   *
   * <p>Convenience method equivalent to {@code executeOperation(scenarioDataSet, operation,
   * dataSource, null)}.
   *
   * @param scenarioDataSet the dataset to use for the operation
   * @param operation the operation to execute
   * @param dataSource the target data source
   * @throws DatabaseTesterException if the operation fails
   */
  public void executeOperation(
      final ScenarioDataSet scenarioDataSet,
      final Operation operation,
      final DataSource dataSource) {
    executeOperation(scenarioDataSet, operation, dataSource, null);
  }

  // ===========================================================================================
  // Dataset Loading
  // ===========================================================================================

  /**
   * Loads CSV dataset from the specified directory.
   *
   * <p>The directory should contain one CSV file per table, where each filename (without extension)
   * becomes the table name. The first row of each CSV file must contain column headers.
   *
   * <p><strong>Directory Structure:</strong>
   *
   * <pre>
   * test-data/
   *   ├── USERS.csv
   *   ├── ORDERS.csv
   *   └── PRODUCTS.csv
   * </pre>
   *
   * @param directory the directory containing CSV files
   * @return the loaded dataset
   * @throws io.github.seijikohara.dbtester.exception.DataSetLoadException if loading fails
   */
  public DataSet loadCsvDataSet(final Path directory) {
    return csvReader.read(directory);
  }

  // ===========================================================================================
  // Assertions - DataSet Level
  // ===========================================================================================

  /**
   * Asserts equality between expected and actual datasets.
   *
   * <p>Compares all tables, rows, and columns. Fails with detailed error message showing the first
   * difference found.
   *
   * @param expected the expected dataset
   * @param actual the actual dataset to compare against
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the datasets do not match
   */
  public void assertEquals(final DataSet expected, final DataSet actual) {
    databaseAssert.assertEquals(
        TypeConverter.toDbUnitDataSet(expected), TypeConverter.toDbUnitDataSet(actual));
  }

  /**
   * Asserts equality between expected and actual datasets with custom failure handling.
   *
   * <p>Allows customization of assertion failure reporting through a user-provided handler.
   *
   * @param expected the expected dataset
   * @param actual the actual dataset to compare against
   * @param failureHandler custom failure handler, or {@code null} for default handler
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the datasets do not match
   */
  public void assertEquals(
      final DataSet expected,
      final DataSet actual,
      final @Nullable AssertionFailureHandler failureHandler) {
    databaseAssert.assertEquals(
        TypeConverter.toDbUnitDataSet(expected),
        TypeConverter.toDbUnitDataSet(actual),
        Optional.ofNullable(failureHandler)
            .map(FailureHandlerAdapter::toDbUnitHandler)
            .orElse(null));
  }

  /**
   * Asserts equality for specific table in datasets, excluding specified columns.
   *
   * <p>Useful for ignoring auto-generated columns (timestamps, IDs) during comparison.
   *
   * @param expected the expected dataset containing table data
   * @param actual the actual dataset to compare against
   * @param tableName the name of the table to compare
   * @param ignoreColumnNames collection of column names to exclude from comparison
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the table data does not match
   */
  public void assertEqualsIgnoreColumns(
      final DataSet expected,
      final DataSet actual,
      final String tableName,
      final Collection<String> ignoreColumnNames) {
    databaseAssert.assertEqualsIgnoreColumns(
        TypeConverter.toDbUnitDataSet(expected),
        TypeConverter.toDbUnitDataSet(actual),
        new TableName(tableName),
        ignoreColumnNames.stream().map(ColumnName::new).toList());
  }

  // ===========================================================================================
  // Assertions - Table Level
  // ===========================================================================================

  /**
   * Asserts equality between expected and actual tables.
   *
   * <p>Compares all rows and columns. Fails with detailed error message showing the first
   * difference found.
   *
   * @param expected the expected table data
   * @param actual the actual table data to compare against
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the tables do not match
   */
  public void assertEquals(final Table expected, final Table actual) {
    databaseAssert.assertEquals(
        TypeConverter.toDbUnitTable(expected), TypeConverter.toDbUnitTable(actual));
  }

  /**
   * Asserts equality between expected and actual tables with additional column names.
   *
   * <p>Column names supplement table definitions. DbUnit infers data types from actual table data.
   *
   * @param expected the expected table data
   * @param actual the actual table data to compare against
   * @param additionalColumnNames additional column names to include in comparison
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the tables do not match
   */
  public void assertEquals(
      final Table expected, final Table actual, final Collection<String> additionalColumnNames) {
    databaseAssert.assertEquals(
        TypeConverter.toDbUnitTable(expected),
        TypeConverter.toDbUnitTable(actual),
        List.of(TypeConverter.toDbUnitColumns(additionalColumnNames)));
  }

  /**
   * Asserts equality between expected and actual tables with custom failure handling.
   *
   * <p>Allows customization of assertion failure reporting through a user-provided handler.
   *
   * @param expected the expected table data
   * @param actual the actual table data to compare against
   * @param failureHandler custom failure handler, or {@code null} for default handler
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the tables do not match
   */
  public void assertEquals(
      final Table expected,
      final Table actual,
      final @Nullable AssertionFailureHandler failureHandler) {
    databaseAssert.assertEquals(
        TypeConverter.toDbUnitTable(expected),
        TypeConverter.toDbUnitTable(actual),
        Optional.ofNullable(failureHandler)
            .map(FailureHandlerAdapter::toDbUnitHandler)
            .orElse(null));
  }

  /**
   * Asserts equality between expected and actual tables, excluding specified columns.
   *
   * <p>Useful for ignoring auto-generated columns (timestamps, IDs) during comparison.
   *
   * @param expected the expected table data
   * @param actual the actual table data to compare against
   * @param ignoreColumnNames collection of column names to exclude from comparison
   * @throws ValidationException if type conversion fails
   * @throws AssertionError if the tables do not match
   */
  public void assertEqualsIgnoreColumns(
      final Table expected, final Table actual, final Collection<String> ignoreColumnNames) {
    databaseAssert.assertEqualsIgnoreColumns(
        TypeConverter.toDbUnitTable(expected),
        TypeConverter.toDbUnitTable(actual),
        ignoreColumnNames.stream().map(ColumnName::new).toList());
  }

  // ===========================================================================================
  // Assertions - Query Based
  // ===========================================================================================

  /**
   * Asserts equality by comparing expected dataset against SQL query results.
   *
   * <p>Retrieves actual data by executing the provided SQL query and compares it against the
   * expected dataset for the specified table.
   *
   * @param expected the expected dataset containing table data
   * @param dataSource database connection source for executing the query
   * @param sqlQuery SQL query to retrieve actual data
   * @param tableName the name of the table in the expected dataset to compare
   * @param ignoreColumnNames collection of column names to exclude from comparison
   * @throws ValidationException if type conversion or connection creation fails
   * @throws AssertionError if the query results do not match the expected data
   */
  public void assertEqualsByQuery(
      final DataSet expected,
      final DataSource dataSource,
      final String sqlQuery,
      final String tableName,
      final Collection<String> ignoreColumnNames) {
    databaseAssert.assertEqualsByQuery(
        TypeConverter.toDbUnitDataSet(expected),
        TypeConverter.toDbUnitConnection(dataSource),
        sqlQuery,
        new TableName(tableName),
        ignoreColumnNames.stream().map(ColumnName::new).toList());
  }

  /**
   * Asserts equality by comparing expected table against SQL query results.
   *
   * <p>Retrieves actual data by executing the provided SQL query and compares it against the
   * expected table.
   *
   * @param expected the expected table data
   * @param dataSource database connection source for executing the query
   * @param tableName the name to assign to the query results
   * @param sqlQuery SQL query to retrieve actual data
   * @param ignoreColumnNames collection of column names to exclude from comparison
   * @throws ValidationException if type conversion or connection creation fails
   * @throws AssertionError if the query results do not match the expected table
   */
  public void assertEqualsByQuery(
      final Table expected,
      final DataSource dataSource,
      final String tableName,
      final String sqlQuery,
      final Collection<String> ignoreColumnNames) {
    databaseAssert.assertEqualsByQuery(
        TypeConverter.toDbUnitTable(expected),
        TypeConverter.toDbUnitConnection(dataSource),
        new TableName(tableName),
        sqlQuery,
        ignoreColumnNames.stream().map(ColumnName::new).toList());
  }

  // ===========================================================================================
  // Verification
  // ===========================================================================================

  /**
   * Verifies database state matches expected dataset.
   *
   * <p>For each table in the expected dataset:
   *
   * <ol>
   *   <li>Retrieves actual data from database
   *   <li>Filters actual data to only include columns present in expected table
   *   <li>Compares filtered actual data against expected data
   * </ol>
   *
   * <p>Only columns present in expected dataset are compared, allowing partial column validation.
   *
   * @param expectedDataSet the expected dataset containing expected table data
   * @param dataSource the database connection source for retrieving actual data
   * @throws ValidationException if verification fails
   */
  public void verifyExpectation(final DataSet expectedDataSet, final DataSource dataSource) {
    try (final var jdbcConnection = dataSource.getConnection()) {
      final var schemaName =
          Optional.ofNullable(jdbcConnection.getSchema()).map(SchemaName::new).orElse(null);
      verifyExpectationWithConnection(expectedDataSet, dataSource, schemaName);
    } catch (final SQLException e) {
      throw new ValidationException("Failed to create database connection", e);
    }
  }

  /**
   * Verifies expectation dataset using established database connection.
   *
   * @param expectedDataSet the expectation dataset containing expected table data
   * @param dataSource the database connection source
   * @param schemaName the database schema name
   * @throws ValidationException if verification fails
   */
  private void verifyExpectationWithConnection(
      final DataSet expectedDataSet,
      final DataSource dataSource,
      final @Nullable SchemaName schemaName) {
    final var dbUnitExpectedDataSet = TypeConverter.toDbUnitDataSet(expectedDataSet);
    final var connection = TypeConverter.toDbUnitConnection(dataSource, schemaName);

    expectedDataSet
        .getTables()
        .forEach(
            expectedTable -> {
              try {
                verifyTable(expectedTable, dbUnitExpectedDataSet, connection);
              } catch (final DataSetException | SQLException e) {
                throw new ValidationException(
                    String.format("Failed to verify table: %s", expectedTable.getName().value()),
                    e);
              }
            });
  }

  /**
   * Verifies single table against actual database state.
   *
   * @param expectedTable the expected table data
   * @param dbUnitExpectedDataSet the DbUnit expected dataset
   * @param connection the DbUnit database connection
   * @throws DataSetException if table verification fails
   * @throws SQLException if database access fails
   */
  private void verifyTable(
      final Table expectedTable,
      final IDataSet dbUnitExpectedDataSet,
      final org.dbunit.database.IDatabaseConnection connection)
      throws DataSetException, SQLException {
    final var tableName = expectedTable.getName();

    // Get expected table from DbUnit dataset
    final var dbUnitExpectedTable = dbUnitExpectedDataSet.getTable(tableName.value());

    // Retrieve actual table data from database
    final var actualCompleteTable = connection.createDataSet().getTable(tableName.value());

    // Extract column names from expected table
    final var expectedColumnNames =
        expectedTable.getColumns().stream().map(ColumnName::value).toList();

    // Filter actual table to only include columns present in expected table
    final var actualFilteredTable =
        DefaultColumnFilter.includedColumnsTable(
            actualCompleteTable, expectedColumnNames.toArray(String[]::new));

    // Perform assertion comparison
    databaseAssert.assertEquals(dbUnitExpectedTable, actualFilteredTable);
  }

  // ===========================================================================================
  // Operation Mapping
  // ===========================================================================================

  /**
   * Converts framework Operation to DbUnit DatabaseOperation.
   *
   * @param operation the framework operation
   * @return the corresponding DbUnit operation
   */
  private static DatabaseOperation toDbUnitOperation(final Operation operation) {
    return switch (operation) {
      case NONE -> DatabaseOperation.NONE;
      case UPDATE -> DatabaseOperation.UPDATE;
      case INSERT -> DatabaseOperation.INSERT;
      case REFRESH -> DatabaseOperation.REFRESH;
      case DELETE -> DatabaseOperation.DELETE;
      case DELETE_ALL -> DatabaseOperation.DELETE_ALL;
      case TRUNCATE_TABLE -> DatabaseOperation.TRUNCATE_TABLE;
      case CLEAN_INSERT -> DatabaseOperation.CLEAN_INSERT;
      case TRUNCATE_INSERT ->
          new CompositeOperation(DatabaseOperation.TRUNCATE_TABLE, DatabaseOperation.INSERT);
    };
  }
}
