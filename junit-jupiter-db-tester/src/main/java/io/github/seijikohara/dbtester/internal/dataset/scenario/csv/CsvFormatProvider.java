package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import io.github.seijikohara.dbtester.api.dataset.DataSetFormatProvider;
import io.github.seijikohara.dbtester.api.dataset.ScenarioDataSet;
import io.github.seijikohara.dbtester.api.domain.FileExtension;
import io.github.seijikohara.dbtester.api.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.api.domain.ScenarioName;
import java.nio.file.Path;
import java.util.Collection;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;

/**
 * CSV format provider for creating datasets from CSV files.
 *
 * <p>This provider handles CSV (Comma-Separated Values) format files. It creates {@link
 * CsvScenarioDataSet} instances that support scenario-based filtering through a special scenario
 * marker column.
 *
 * <h2>CSV File Structure</h2>
 *
 * <p>CSV files should follow these conventions:
 *
 * <ul>
 *   <li>File name matches the table name
 *   <li>First row contains column headers
 *   <li>Optional scenario marker column for scenario-based filtering
 *   <li>Standard CSV format with comma separators
 *   <li>Empty cells represent NULL values in the database
 * </ul>
 *
 * <h2>Example CSV File</h2>
 *
 * <pre>
 * ID,NAME,[Scenario]
 * 1,Alice,scenario1
 * 2,Bob,scenario1
 * 3,Charlie,scenario2
 * </pre>
 *
 * <h2>Auto-Registration</h2>
 *
 * <p>This provider is discovered automatically via {@link java.util.ServiceLoader}. No manual
 * registration is required when the module descriptor or {@code META-INF/services} entry is on the
 * classpath.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This class is stateless and thread-safe. All methods are side-effect free.
 *
 * @see CsvScenarioDataSet
 * @see DataSetFormatProvider
 * @see io.github.seijikohara.dbtester.api.dataset.DataSetFormatRegistry
 */
public final class CsvFormatProvider implements DataSetFormatProvider {

  /** Creates a new CSV format provider. */
  public CsvFormatProvider() {}

  @Override
  public FileExtension supportedFileExtension() {
    return new FileExtension("csv");
  }

  @Override
  public ScenarioDataSet createDataSet(
      final Path directory,
      final Collection<ScenarioName> scenarioNames,
      final ScenarioMarker scenarioMarker,
      final @Nullable DataSource dataSource) {
    return new CsvScenarioDataSet(directory, scenarioNames, scenarioMarker, dataSource);
  }
}
