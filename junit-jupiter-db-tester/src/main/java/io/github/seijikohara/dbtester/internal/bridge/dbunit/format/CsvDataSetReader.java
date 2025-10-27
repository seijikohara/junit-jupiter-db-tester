package io.github.seijikohara.dbtester.internal.bridge.dbunit.format;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitDataSetAdapter;
import io.github.seijikohara.dbtester.internal.dataset.DataSet;
import java.nio.file.Path;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.csv.CsvDataSet;

/**
 * CSV format implementation of DataSetReader.
 *
 * <p>Reads CSV files into framework DataSet using DbUnit's CSV parser. This reader isolates
 * DbUnit's CSV parsing logic from the framework core, providing a framework-independent interface.
 *
 * <h2>CSV Format</h2>
 *
 * <p>DbUnit's CSV format follows these conventions:
 *
 * <ul>
 *   <li>Each CSV file represents one table (filename = table name)
 *   <li>First row contains column headers
 *   <li>Empty cells represent NULL values
 *   <li>All files must be in the same directory
 * </ul>
 *
 * <h2>Directory Structure</h2>
 *
 * <pre>
 * test-data/
 *   ├── USERS.csv
 *   ├── ORDERS.csv
 *   └── PRODUCTS.csv
 * </pre>
 *
 * <h2>Example CSV File (USERS.csv)</h2>
 *
 * <pre>
 * ID,NAME,EMAIL
 * 1,Alice,alice@example.com
 * 2,Bob,bob@example.com
 * </pre>
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * final var reader = new CsvDataSetReader();
 * final var dataSet = reader.read(Path.of("src/test/resources/test-data"));
 * }</pre>
 *
 * <h2>DbUnit Isolation</h2>
 *
 * <p>This class is the ONLY place where DbUnit's CSV parsing is used. All DbUnit dependencies are
 * isolated to the bridge package, allowing the rest of the framework to remain DbUnit-independent.
 *
 * <h2>Path vs File</h2>
 *
 * <p>The framework uses {@link Path} exclusively throughout. This class performs the ONLY {@code
 * Path -> File} conversion in the entire framework, required by DbUnit's {@link CsvDataSet} API.
 *
 * <h2>Null Safety</h2>
 *
 * <p>This is an INTERNAL class (package-private). NullAway enforces null safety at compile time; no
 * runtime {@code Objects.requireNonNull()} checks are needed.
 *
 * @see DataSetReader
 * @see CsvDataSet
 * @see DbUnitDataSetAdapter
 */
public final class CsvDataSetReader implements DataSetReader {

  /**
   * Creates CSV dataset reader.
   *
   * <p>This is a stateless utility class - instances can be reused or created as needed.
   */
  public CsvDataSetReader() {}

  /**
   * Reads CSV files from the specified directory into a DataSet.
   *
   * <p>This method:
   *
   * <ol>
   *   <li>Converts Path to File (DbUnit's CsvDataSet requires java.io.File)
   *   <li>Creates a DbUnit {@link CsvDataSet} from the directory
   *   <li>Wraps it in a {@link DbUnitDataSetAdapter} to hide DbUnit types
   *   <li>Returns the framework-independent {@link DataSet}
   * </ol>
   *
   * <p><strong>Note:</strong> This is the ONLY place in the entire framework where {@code Path ->
   * File} conversion occurs. All other code uses {@link Path} exclusively.
   *
   * @param directory the directory containing CSV files (one file per table)
   * @return the loaded dataset
   * @throws DataSetLoadException if CSV loading fails
   */
  @Override
  public DataSet read(final Path directory) {
    try {
      // Convert Path to File - DbUnit's CsvDataSet only accepts java.io.File
      // This is the ONLY Path->File conversion in the entire framework
      final var directoryFile = directory.toFile();

      // Use DbUnit's CSV parser to load all CSV files in the directory
      final var csvDataSet = new CsvDataSet(directoryFile);

      // Wrap the DbUnit dataset to hide DbUnit types from the framework
      return new DbUnitDataSetAdapter(csvDataSet);
    } catch (final DataSetException e) {
      throw new DataSetLoadException(
          String.format("Failed to load CSV dataset from: %s", directory), e);
    }
  }
}
