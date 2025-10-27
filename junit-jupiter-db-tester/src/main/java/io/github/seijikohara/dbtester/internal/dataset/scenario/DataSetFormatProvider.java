package io.github.seijikohara.dbtester.internal.dataset.scenario;

import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Provider interface for creating dataset instances from different file formats.
 *
 * <p>This interface defines the contract for dataset format providers that can load test data from
 * various file formats (CSV, TSV, JSON, etc.). Each implementation supports a specific file
 * extension and knows how to create a {@link ScenarioDataSet} from files of that format.
 *
 * <h2>Format Detection</h2>
 *
 * <p>Format providers are selected automatically based on file extensions found in the test data
 * directory. The {@link #supportedExtension()} method returns the file extension this provider
 * handles (e.g., ".csv", ".tsv").
 *
 * <h2>Implementation Requirements</h2>
 *
 * <p>Implementations must:
 *
 * <ul>
 *   <li>Be stateless and thread-safe
 *   <li>Return a unique file extension from {@link #supportedExtension()}
 *   <li>Create datasets that support scenario filtering
 *   <li>Throw {@link io.github.seijikohara.dbtester.exception.DataSetLoadException} on errors
 * </ul>
 *
 * <h2>Example Implementation</h2>
 *
 * <pre>{@code
 * public final class CsvFormatProvider implements DataSetFormatProvider {
 *   @Override
 *   public String supportedExtension() {
 *     return ".csv";
 *   }
 *
 *   @Override
 *   public ScenarioDataSet createDataSet(
 *       Path directory, Collection<ScenarioName> scenarioNames, ScenarioMarker scenarioMarker) {
 *     return new CsvScenarioDataSet(directory, scenarioNames, scenarioMarker);
 *   }
 * }
 * }</pre>
 *
 * @see ScenarioDataSet
 * @see DataSetFormatRegistry
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvFormatProvider
 */
public interface DataSetFormatProvider {

  /**
   * Returns the file extension supported by this provider.
   *
   * <p>The extension should include the leading dot (e.g., ".csv", ".tsv", ".json"). This is used
   * to detect which provider should handle files in a given directory.
   *
   * @return the supported file extension (with leading dot)
   */
  String supportedExtension();

  /**
   * Creates a scenario dataset from files in the specified directory.
   *
   * <p>This method reads all files with the {@link #supportedExtension()} from the directory and
   * creates a {@link ScenarioDataSet} instance. The dataset should apply scenario filtering based
   * on the provided scenario names and marker.
   *
   * <p>If scenario names are provided, only rows with a matching scenario marker column value
   * should be included. If files don't have a scenario marker column, all rows should be included.
   *
   * @param directory the directory path containing data files
   * @param scenarioNames the scenario names to filter rows; if empty, all rows are included
   * @param scenarioMarker the scenario marker identifying the special column for scenario filtering
   * @return the loaded scenario dataset
   * @throws io.github.seijikohara.dbtester.exception.DataSetLoadException if the dataset cannot be
   *     created or loaded
   */
  ScenarioDataSet createDataSet(
      Path directory, Collection<ScenarioName> scenarioNames, ScenarioMarker scenarioMarker);
}
