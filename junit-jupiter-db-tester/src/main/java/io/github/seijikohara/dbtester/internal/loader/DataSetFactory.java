package io.github.seijikohara.dbtester.internal.loader;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Factory for creating dataset instances from various file formats.
 *
 * <p>This class encapsulates the logic for loading datasets with scenario filtering. It
 * automatically detects the file format based on file extensions and delegates to the appropriate
 * format provider from the registry.
 *
 * <h2>Format Detection</h2>
 *
 * <p>The factory detects the format by examining files in the test data directory. It looks for the
 * first data file and determines the format from its extension:
 *
 * <ul>
 *   <li>{@code .csv} - CSV (Comma-Separated Values) format
 * </ul>
 *
 * <h2>Dataset Creation</h2>
 *
 * <p>The factory creates datasets by:
 *
 * <ol>
 *   <li>Detecting the file format from directory contents
 *   <li>Retrieving the appropriate format provider from {@link
 *       io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry}
 *   <li>Delegating dataset creation to the provider
 *   <li>Applying scenario filtering to include only relevant rows
 * </ol>
 *
 * <h2>Scenario Filtering</h2>
 *
 * <p>When data files include a scenario marker column (e.g., {@code #scenario}), only rows matching
 * the specified scenario names are included in the dataset. This allows organizing multiple test
 * scenarios within the same data files.
 *
 * <h2>Error Handling</h2>
 *
 * <p>The factory provides detailed error messages for:
 *
 * <ul>
 *   <li>Unsupported file formats
 *   <li>Empty directories (no data files)
 *   <li>File parsing errors
 *   <li>I/O errors when reading files
 * </ul>
 *
 * <h2>Extensibility</h2>
 *
 * <p>New file formats can be supported by implementing {@link
 * io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatProvider} and registering
 * it with {@link io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry}.
 * The registry auto-discovers providers via reflection.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This class is stateless and thread-safe. All methods are side-effect free.
 *
 * @see ScenarioDataSet
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvFormatProvider
 */
final class DataSetFactory {

  /** Creates a new dataset factory. */
  DataSetFactory() {}

  /**
   * Loads a scenario dataset from a directory.
   *
   * <p>This method automatically detects the file format by examining files in the directory,
   * retrieves the appropriate provider from the registry, and creates a {@link ScenarioDataSet}
   * instance. Each data file becomes a table in the dataset.
   *
   * <p>If scenario names are provided, only rows with a matching scenario marker column value are
   * included. If files don't have a scenario marker column, all rows are included.
   *
   * @param directory the directory path containing data files (one file per table)
   * @param scenarioNames the scenario names to filter rows; if empty, all rows are included
   * @param scenarioMarker the scenario marker identifying the special column for scenario filtering
   * @return the loaded scenario dataset
   * @throws DataSetLoadException if the dataset cannot be created or loaded (unsupported format,
   *     empty directory, I/O errors, etc.)
   */
  ScenarioDataSet createDataSet(
      final Path directory,
      final Collection<ScenarioName> scenarioNames,
      final ScenarioMarker scenarioMarker) {

    final var extension = detectFileExtension(directory);
    final var provider = DataSetFormatRegistry.getProvider(extension);

    return provider.createDataSet(directory, scenarioNames, scenarioMarker);
  }

  /**
   * Detects the file extension from the first data file in the directory.
   *
   * <p>Only considers files with extensions supported by registered format providers (e.g., .csv).
   * Ignores metadata files like table-ordering.txt.
   *
   * @param directory the directory to scan
   * @return the file extension (with leading dot, e.g., ".csv")
   * @throws DataSetLoadException if no data files are found or I/O error occurs
   */
  private String detectFileExtension(final Path directory) {
    final var supportedExtensions = DataSetFormatRegistry.getSupportedExtensions();

    try (final var stream = Files.newDirectoryStream(directory)) {
      return StreamSupport.stream(stream.spliterator(), false)
          .filter(Files::isRegularFile)
          .map(
              file -> {
                final var fileName = file.getFileName().toString();
                final var dotIndex = fileName.lastIndexOf('.');
                return dotIndex > 0 ? fileName.substring(dotIndex) : null;
              })
          .filter(Objects::nonNull)
          .filter(supportedExtensions::contains)
          .findFirst()
          .orElseThrow(
              () ->
                  new DataSetLoadException(
                      String.format(
                          "No data files found in directory: %s. Ensure the directory contains at least one data file (e.g., .csv).",
                          directory)));
    } catch (final IOException e) {
      throw new DataSetLoadException(
          String.format("Failed to read directory: %s. Cause: %s", directory, e.getMessage()), e);
    }
  }
}
