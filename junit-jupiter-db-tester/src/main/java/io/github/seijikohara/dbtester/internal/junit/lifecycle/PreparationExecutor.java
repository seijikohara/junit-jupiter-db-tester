package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.api.context.TestContext;
import io.github.seijikohara.dbtester.api.dataset.ScenarioDataSet;
import io.github.seijikohara.dbtester.api.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.api.exception.DatabaseTesterException;
import io.github.seijikohara.dbtester.api.operation.Operation;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes preparation datasets prior to each test invocation.
 *
 * <p>The executor receives a {@link ScenarioDataSet} from {@link TestLifecycle}, resolves its
 * configured {@link DataSource}, and delegates the database interaction to {@link DatabaseBridge}.
 * The bridge applies the {@link Operation} declared in the {@link TestContext} configuration,
 * typically {@code CLEAN_INSERT}, ensuring that the database is hydrated with the expected state
 * before user test logic runs.
 *
 * <p>The class is intentionally stateless: all dependencies are supplied via parameters, and the
 * only observable side effects are routed through {@link DatabaseBridge} and structured logging.
 * Any {@link DatabaseTesterException} emitted by the bridge is translated into a {@link
 * DataSetLoadException} so that callers observe consistent loader semantics.
 *
 * @see ExpectationVerifier
 * @see TestLifecycle
 * @see DatabaseBridge
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
    try {
      DatabaseBridge.getInstance().executeOperation(dataSet, operation, dataSource);
    } catch (final DatabaseTesterException e) {
      throw new DataSetLoadException(
          String.format("Failed to execute preparation for %s", context.testMethod().getName()), e);
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
