package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.api.context.TestContext;
import io.github.seijikohara.dbtester.api.dataset.ScenarioDataSet;
import io.github.seijikohara.dbtester.api.exception.ValidationException;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates expectation datasets against the live database after test execution.
 *
 * <p>The verifier delegates to {@link DatabaseBridge} for all DbUnit operations. The bridge opens a
 * connection through the dataset's {@link DataSource}, applies column filtering so that only
 * declared columns participate in comparisons, and raises {@link ValidationException} when the
 * observed database state deviates from the expected dataset.
 *
 * <p>Like {@link PreparationExecutor}, this class is stateless and thread-safe. It performs
 * structured logging to aid debugging and rewraps any {@link ValidationException} thrown by the
 * bridge with additional test context so failures remain actionable in the calling layer.
 *
 * @see PreparationExecutor
 * @see TestLifecycle
 * @see DatabaseBridge
 */
final class ExpectationVerifier {

  /** Logger for verification progress and error reporting. */
  private static final Logger logger = LoggerFactory.getLogger(ExpectationVerifier.class);

  /** Creates an expectation verifier. */
  ExpectationVerifier() {}

  /**
   * Verifies an expectation dataset against actual database state.
   *
   * @param context the test context
   * @param dataSet the expectation dataset
   * @throws ValidationException if verification fails
   */
  void verify(final TestContext context, final ScenarioDataSet dataSet) {
    try {
      final var tableCount = dataSet.getTables().size();
      logger.info(
          "Validating expectation dataset for {}: {} tables",
          context.testMethod().getName(),
          tableCount);

      final var dataSource = requireDataSource(dataSet);

      // Delegate all DbUnit operations to the unified bridge
      DatabaseBridge.getInstance().verifyExpectation(dataSet, dataSource);

      logger.info(
          "Expectation validation completed successfully for {}: {} tables",
          context.testMethod().getName(),
          tableCount);
    } catch (final ValidationException e) {
      throw new ValidationException(
          String.format(
              "Failed to verify expectation dataset for %s", context.testMethod().getName()),
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
