package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.exception.ValidationException;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.loader.DataSetLoader;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;

/**
 * Immutable test lifecycle orchestrator.
 *
 * <p>This class coordinates the execution of database test lifecycle phases, including data
 * preparation before tests and expectation validation after tests. It delegates to specialized
 * executors and verifiers while maintaining immutability and thread safety.
 *
 * <h2>Lifecycle Phases</h2>
 *
 * <ol>
 *   <li><strong>Before Each Test:</strong>
 *       <ul>
 *         <li>Load preparation datasets using the configured loader
 *         <li>Execute datasets to populate the database
 *         <li>Log preparation progress
 *       </ul>
 *   <li><strong>Test Execution:</strong> (handled by JUnit)
 *   <li><strong>After Each Test:</strong>
 *       <ul>
 *         <li>Load expectation datasets using the configured loader
 *         <li>Verify database state against expectations
 *         <li>Log verification results
 *       </ul>
 * </ol>
 *
 * <h2>Design Rationale</h2>
 *
 * <p>This class follows the orchestrator pattern, coordinating multiple specialized components
 * without containing business logic itself:
 *
 * <ul>
 *   <li>DataSetLoader - resolves and loads datasets
 *   <li>PreparationExecutor - executes preparation datasets
 *   <li>ExpectationVerifier - verifies expectation datasets
 * </ul>
 *
 * <p>By keeping this class immutable with all dependencies injected via constructor, we achieve
 * thread safety and testability.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * TestLifecycle lifecycle = new TestLifecycle(loader);
 *
 * // Before each test
 * lifecycle.executePreparation(testContext);
 *
 * // After each test
 * lifecycle.executeVerification(testContext);
 * }</pre>
 *
 * <p>This class is intended for internal framework use.
 *
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 * @see PreparationExecutor
 * @see ExpectationVerifier
 */
public final class TestLifecycle {

  /** Dataset loader for resolving preparation and expectation datasets. */
  private final DataSetLoader dataSetLoader;

  /** Executor for preparation datasets. */
  private final PreparationExecutor preparationExecutor;

  /** Verifier for expectation datasets. */
  private final ExpectationVerifier expectationVerifier;

  /**
   * Creates a test lifecycle orchestrator.
   *
   * @param dataSetLoader the dataset loader (must not be null)
   */
  public TestLifecycle(final DataSetLoader dataSetLoader) {
    this.dataSetLoader = dataSetLoader;
    this.preparationExecutor = new PreparationExecutor();
    this.expectationVerifier = new ExpectationVerifier();
  }

  /**
   * Executes the preparation phase before test execution.
   *
   * <p>This method loads and executes all preparation datasets for the test method. When multiple
   * datasets are specified, they are merged together and the operation is applied once to the
   * merged dataset.
   *
   * @param context the test context
   * @throws DataSetLoadException if loading or executing preparation datasets fails
   */
  public void executePreparation(final TestContext context) {
    try {
      final var preparationDataSets = dataSetLoader.loadPreparationDataSets(context);
      if (preparationDataSets.isEmpty()) {
        return;
      }

      final var mergedDataSet = mergeDataSetsIfNeeded(preparationDataSets);
      preparationExecutor.execute(context, mergedDataSet);
    } catch (final DataSetLoadException e) {
      throw new DataSetLoadException(
          String.format("Failed to execute preparation for %s", context.testMethod().getName()), e);
    }
  }

  /**
   * Executes the verification phase after test execution.
   *
   * <p>This method loads and verifies all expectation datasets for the test method. When multiple
   * datasets are specified, they are merged together before validation.
   *
   * @param context the test context
   * @throws ValidationException if loading or verifying expectation datasets fails
   */
  public void executeVerification(final TestContext context) {
    try {
      final var expectationDataSets = dataSetLoader.loadExpectationDataSets(context);
      if (expectationDataSets.isEmpty()) {
        return;
      }

      final var mergedDataSet = mergeDataSetsIfNeeded(expectationDataSets);
      expectationVerifier.verify(context, mergedDataSet);
    } catch (final ValidationException | DataSetLoadException e) {
      throw new ValidationException(
          String.format("Failed to execute verification for %s", context.testMethod().getName()),
          e);
    }
  }

  /**
   * Merges multiple datasets into one if necessary.
   *
   * <p>If only one dataset is provided, returns it as-is. If multiple datasets are provided, merges
   * them by combining all tables. All datasets must share the same DataSource.
   *
   * @param dataSets the collection of datasets to merge
   * @return a single merged dataset
   * @throws DataSetLoadException if datasets cannot be merged or use different DataSources
   */
  private ScenarioDataSet mergeDataSetsIfNeeded(final Collection<ScenarioDataSet> dataSets) {
    if (dataSets.size() == 1) {
      return dataSets.iterator().next();
    }

    final var firstDataSource = dataSets.iterator().next().getDataSource();
    final var allSameDataSource =
        dataSets.stream()
            .allMatch(scenarioDataSet -> scenarioDataSet.getDataSource().equals(firstDataSource));

    if (!allSameDataSource) {
      throw new DataSetLoadException(
          "Cannot merge datasets with different DataSources. All @DataSet annotations in a single @Preparation or @Expectation must use the same dataSourceName.");
    }

    return new MergedScenarioDataSet(dataSets, firstDataSource.orElse(null));
  }

  /**
   * Wrapper for merged datasets.
   *
   * <p>This internal class merges multiple ScenarioDataSets by combining all their tables,
   * maintaining compatibility with the Collection-based ScenarioDataSet interface.
   */
  private static final class MergedScenarioDataSet extends ScenarioDataSet {

    /** All tables from merged datasets. */
    private final List<Table> tables;

    /**
     * Creates a merged scenario dataset.
     *
     * @param dataSets the datasets to merge
     * @param dataSource the DataSource (all merged datasets must use the same one, nullable)
     */
    MergedScenarioDataSet(
        final Collection<ScenarioDataSet> dataSets, final @Nullable DataSource dataSource) {
      this.tables = dataSets.stream().flatMap(dataSet -> dataSet.getTables().stream()).toList();
      setDataSource(dataSource);
    }

    @Override
    public List<Table> getTables() {
      return tables;
    }
  }
}
