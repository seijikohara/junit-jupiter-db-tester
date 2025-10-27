package io.github.seijikohara.dbtester.internal.loader;

import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestContext;
import java.util.List;

/**
 * Strategy interface for loading test datasets from various sources.
 *
 * <p>This interface defines the contract for dataset loading strategies, allowing the framework to
 * support different data sources and file organization schemes. Implementations resolve dataset
 * locations, load the data, and apply any necessary filtering or transformation.
 *
 * <h2>Loading Strategy</h2>
 *
 * <p>The loader is responsible for:
 *
 * <ul>
 *   <li>Resolving dataset file locations based on test class and method metadata
 *   <li>Reading and parsing dataset files (typically CSV files)
 *   <li>Applying scenario filtering based on annotation configuration
 *   <li>Associating datasets with the appropriate data sources
 * </ul>
 *
 * <h2>Default Implementation</h2>
 *
 * <p>The framework provides {@link TestClassNameBasedDataSetLoader} as the default implementation,
 * which uses convention-based resolution:
 *
 * <ul>
 *   <li>Preparation data: {@code classpath:[package]/[TestClass]/}
 *   <li>Expectation data: {@code classpath:[package]/[TestClass]/expected/}
 * </ul>
 *
 * <h2>Custom Implementations</h2>
 *
 * <p>Custom loaders can support alternative data sources such as databases, Excel files, or remote
 * services. To use a custom loader, configure it in the framework {@code Configuration} before
 * running tests.
 *
 * @see TestClassNameBasedDataSetLoader
 * @see io.github.seijikohara.dbtester.config.Configuration
 */
public interface DataSetLoader {

  /**
   * Loads datasets for preparing the database before test execution.
   *
   * <p>This method is called by {@link
   * io.github.seijikohara.dbtester.extension.DatabaseTestExtension} before each test method runs.
   * The returned datasets are applied to the database using the configured operations (typically
   * {@link io.github.seijikohara.dbtester.api.operation.Operation#CLEAN_INSERT}).
   *
   * @param context the test execution context containing test metadata and configuration
   * @return list of datasets to load into the database
   */
  List<ScenarioDataSet> loadPreparationDataSets(TestContext context);

  /**
   * Loads datasets for validating the database state after test execution.
   *
   * <p>This method is called by {@link
   * io.github.seijikohara.dbtester.extension.DatabaseTestExtension} after each test method
   * completes. The returned datasets are compared with the actual database state to verify test
   * results.
   *
   * @param context the test execution context containing test metadata and configuration
   * @return list of datasets to validate against the database
   */
  List<ScenarioDataSet> loadExpectationDataSets(TestContext context);
}
