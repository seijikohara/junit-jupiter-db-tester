package io.github.seijikohara.dbtester.api.loader;

import io.github.seijikohara.dbtester.api.context.TestContext;
import io.github.seijikohara.dbtester.api.dataset.ScenarioDataSet;
import java.util.List;

/** Strategy SPI that resolves {@link ScenarioDataSet} instances for each test phase. */
public interface DataSetLoader {

  /**
   * Loads datasets for preparing the database before test execution.
   *
   * <p>This method is called by {@link
   * io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension} before each test method
   * runs. The returned datasets are applied to the database using the configured operations
   * (typically {@link io.github.seijikohara.dbtester.api.operation.Operation#CLEAN_INSERT}).
   *
   * @param context the test execution context containing test metadata and configuration
   * @return list of datasets to load into the database
   */
  List<ScenarioDataSet> loadPreparationDataSets(TestContext context);

  /**
   * Loads datasets for validating the database state after test execution.
   *
   * <p>This method is called by {@link
   * io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension} after each test method
   * completes. The returned datasets are compared with the actual database state to verify test
   * results.
   *
   * @param context the test execution context containing test metadata and configuration
   * @return list of datasets to validate against the database
   */
  List<ScenarioDataSet> loadExpectationDataSets(TestContext context);
}
