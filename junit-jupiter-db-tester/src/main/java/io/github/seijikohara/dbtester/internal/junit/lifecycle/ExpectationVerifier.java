package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.exception.ValidationException;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verifies expectation datasets against actual database state.
 *
 * <p>This class validates that the actual database state after test execution matches the expected
 * state defined in expectation datasets. It delegates all verification logic to {@link
 * DatabaseBridge}, which handles database connections, column filtering, and assertion comparisons.
 *
 * <h2>Verification Process</h2>
 *
 * <ol>
 *   <li>Extract data source from the dataset
 *   <li>Delegate verification to {@link DatabaseBridge}
 *   <li>The bridge handles:
 *       <ul>
 *         <li>Database connection creation
 *         <li>Retrieving actual data from database
 *         <li>Filtering columns to match expected dataset
 *         <li>Performing assertion comparison
 *       </ul>
 * </ol>
 *
 * <h2>Column Filtering</h2>
 *
 * <p>Only columns present in the expected dataset are compared, allowing tests to:
 *
 * <ul>
 *   <li>Ignore auto-generated columns (IDs, timestamps)
 *   <li>Focus on business-relevant columns
 *   <li>Validate partial table data
 * </ul>
 *
 * <h2>Stateless Design</h2>
 *
 * <p>This class is stateless and thread-safe. All state is passed as method parameters, making it
 * suitable for concurrent test execution.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * ExpectationVerifier verifier = new ExpectationVerifier();
 * verifier.verify(testContext, expectationDataSet);
 * }</pre>
 *
 * <p>This class is package-private and intended for internal use only.
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
