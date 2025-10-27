package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatProvider;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import java.nio.file.Path;
import java.util.Collection;

/**
 * CSV format provider for creating datasets from CSV files.
 *
 * <p>This provider handles CSV (Comma-Separated Values) format files with the {@code .csv}
 * extension. It creates {@link CsvScenarioDataSet} instances that support scenario-based filtering
 * through a special scenario marker column.
 *
 * <h2>CSV File Structure</h2>
 *
 * <p>CSV files should follow these conventions:
 *
 * <ul>
 *   <li>File name matches the table name (e.g., {@code USERS.csv})
 *   <li>First row contains column headers
 *   <li>Optional scenario marker column for scenario-based filtering (e.g., {@code #scenario})
 *   <li>Standard CSV format with comma separators
 *   <li>Empty cells represent NULL values in the database
 * </ul>
 *
 * <h2>Example CSV File</h2>
 *
 * <pre>
 * ID,NAME,#scenario
 * 1,Alice,active
 * 2,Bob,active
 * 3,Charlie,inactive
 * </pre>
 *
 * <h2>Auto-Registration</h2>
 *
 * <p>This provider is automatically registered by {@link
 * io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry} via reflection.
 * No manual registration is required.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This class is stateless and thread-safe. All methods are side-effect free.
 *
 * @see CsvScenarioDataSet
 * @see DataSetFormatProvider
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry
 */
public final class CsvFormatProvider implements DataSetFormatProvider {

  /** Creates a new CSV format provider. */
  public CsvFormatProvider() {}

  @Override
  public String supportedExtension() {
    return ".csv";
  }

  @Override
  public ScenarioDataSet createDataSet(
      final Path directory,
      final Collection<ScenarioName> scenarioNames,
      final ScenarioMarker scenarioMarker) {
    return new CsvScenarioDataSet(directory, scenarioNames, scenarioMarker);
  }
}
