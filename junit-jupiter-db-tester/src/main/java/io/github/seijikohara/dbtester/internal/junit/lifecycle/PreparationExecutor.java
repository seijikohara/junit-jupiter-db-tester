package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.api.operation.Operation;
import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.domain.SchemaName;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes preparation datasets before test execution.
 *
 * <p>This class handles the execution of preparation datasets, loading test data into the database
 * before each test method runs. It performs the following operations:
 *
 * <ol>
 *   <li>Extract data source from the dataset
 *   <li>Create database connection
 *   <li>Execute the configured database operation (typically CLEAN_INSERT)
 *   <li>Clean up resources
 * </ol>
 *
 * <h2>Stateless Design</h2>
 *
 * <p>This class is stateless and thread-safe. All state is passed as method parameters, making it
 * suitable for concurrent test execution.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * PreparationExecutor executor = new PreparationExecutor();
 * executor.execute(testContext, preparationDataSet);
 * }</pre>
 *
 * <p>This class is package-private and intended for internal use only.
 *
 * @see ExpectationVerifier
 * @see TestLifecycle
 */
final class PreparationExecutor {

  /** Logger for tracking preparation execution. */
  private static final Logger logger = LoggerFactory.getLogger(PreparationExecutor.class);

  /** Creates a preparation executor. */
  PreparationExecutor() {}

  /**
   * Executes a preparation dataset.
   *
   * @param context the test context
   * @param dataSet the preparation dataset to execute
   * @throws DataSetLoadException if dataset execution fails
   */
  void execute(final TestContext context, final ScenarioDataSet dataSet) {
    final var tables = dataSet.getTables();
    logger.info(
        "Executing preparation dataset for {}: {} tables",
        context.testMethod().getName(),
        tables.size());

    final var dataSource = requireDataSource(dataSet);
    final var operation = context.configuration().operations().preparation();

    executeDatabaseOperation(context, dataSet, operation, dataSource);

    logger.info("Successfully executed preparation dataset for {}", context.testMethod().getName());
  }

  /**
   * Executes the database operation using the facade layer.
   *
   * @param context the test context
   * @param dataSet the preparation dataset
   * @param operation the database operation to execute
   * @param dataSource the data source
   * @throws DataSetLoadException if the database operation fails
   */
  private void executeDatabaseOperation(
      final TestContext context,
      final ScenarioDataSet dataSet,
      final Operation operation,
      final DataSource dataSource) {
    try (final var jdbcConnection = dataSource.getConnection()) {
      // Use the connection's default schema to avoid AmbiguousTableNameException
      // while maintaining compatibility with different databases (H2, Derby, PostgreSQL, etc.)
      final var schemaName =
          Optional.ofNullable(jdbcConnection.getSchema()).map(SchemaName::new).orElse(null);

      // Delegate to unified DatabaseBridge for database operations
      DatabaseBridge.getInstance().executeOperation(dataSet, operation, dataSource, schemaName);
    } catch (final SQLException e) {
      throw new DataSetLoadException(
          String.format("Failed to get database connection for %s", context.testMethod().getName()),
          e);
    }
  }

  /**
   * Retrieves the data source from a dataset, throwing an exception if not configured.
   *
   * @param dataSet the dataset containing the data source
   * @return the data source
   * @throws IllegalStateException if the data source is not configured
   */
  private DataSource requireDataSource(final ScenarioDataSet dataSet) {
    return dataSet
        .getDataSource()
        .orElseThrow(() -> new IllegalStateException("DataSource not configured for dataset"));
  }
}
