package io.github.seijikohara.dbtester.internal.dataset;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Utility for resolving and managing table-ordering.txt files.
 *
 * <p>This class provides format-independent table ordering functionality. When a table-ordering.txt
 * file is present in the data directory, it is used as-is. When absent, a table-ordering.txt file
 * is automatically created with tables listed alphabetically.
 *
 * <h2>Table Ordering Behavior</h2>
 *
 * <ul>
 *   <li><strong>With table-ordering.txt:</strong> The existing file is used for table ordering
 *   <li><strong>Without table-ordering.txt:</strong> A table-ordering.txt file is automatically
 *       created with tables ordered alphabetically by file name
 * </ul>
 *
 * <h2>File Structure</h2>
 *
 * <p>The table-ordering.txt file should contain one table name per line:
 *
 * <pre>
 * USERS
 * ORDERS
 * AUDIT_LOG
 * </pre>
 *
 * <h2>Subclass Requirements</h2>
 *
 * <p>Subclasses must implement {@link #getFileExtension()} to specify the file extension they
 * handle (e.g., ".csv", ".tsv"). This is used to scan directories and extract table names.
 *
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioDataSet
 */
public abstract class TableOrderingResolver {

  /** The name of the table ordering file. */
  private static final String TABLE_ORDERING_FILE = "table-ordering.txt";

  /** Creates a table ordering resolver. */
  protected TableOrderingResolver() {}

  /**
   * Returns the file extension handled by this dataset format.
   *
   * <p>The extension should include the leading dot (e.g., ".csv", ".tsv"). This is used to scan
   * directories for data files and extract table names.
   *
   * @return the file extension with leading dot
   */
  protected abstract String getFileExtension();

  /**
   * Ensures table-ordering.txt exists in the directory.
   *
   * <p>If table-ordering.txt exists, this method does nothing. If the file doesn't exist, a default
   * ordering file is created with tables sorted alphabetically.
   *
   * @param directory the directory path containing data files
   * @return the directory path (possibly with a generated table-ordering.txt)
   * @throws DataSetLoadException if file operations fail
   */
  public final Path ensureTableOrdering(final Path directory) {
    final var orderingFile = directory.resolve(TABLE_ORDERING_FILE);
    if (!Files.exists(orderingFile)) {
      createDefaultTableOrdering(directory, orderingFile);
    }
    return directory;
  }

  /**
   * Creates a default table-ordering.txt file with alphabetically sorted table names.
   *
   * <p>This method scans the directory for data files with the format-specific extension, extracts
   * table names (file names without extension), sorts them alphabetically, and writes them to
   * table-ordering.txt.
   *
   * @param directory the directory path containing data files
   * @param orderingFile the table-ordering.txt file path to create
   * @throws DataSetLoadException if file operations fail
   */
  private void createDefaultTableOrdering(final Path directory, final Path orderingFile) {
    final var tableNames = extractTableNames(directory);
    writeTableOrdering(orderingFile, tableNames);
  }

  /**
   * Extracts table names from data files in a directory.
   *
   * @param directory the directory path containing data files
   * @return list of table names sorted alphabetically
   * @throws DataSetLoadException if directory listing fails
   */
  private List<TableName> extractTableNames(final Path directory) {
    final var extension = getFileExtension();
    return getDataFileStream(directory, extension)
        .map(Path::getFileName)
        .map(Path::toString)
        .filter(name -> name.toLowerCase(Locale.ROOT).endsWith(extension))
        .map(name -> name.substring(0, name.length() - extension.length()))
        .map(TableName::new)
        .sorted()
        .toList();
  }

  /**
   * Writes table names to a table-ordering.txt file.
   *
   * @param orderingFile the table-ordering.txt file path to write
   * @param tableNames the list of table names
   * @throws DataSetLoadException if writing to the file fails
   */
  private static void writeTableOrdering(
      final Path orderingFile, final Collection<TableName> tableNames) {
    try {
      final var tableNameStrings = tableNames.stream().map(TableName::value).toList();
      Files.write(orderingFile, tableNameStrings);
    } catch (final IOException e) {
      throw new DataSetLoadException(
          String.format("Failed to write table ordering file: %s", orderingFile), e);
    }
  }

  /**
   * Gets a stream of data files in a directory.
   *
   * @param directory the directory path to scan
   * @param extension the file extension to filter (with leading dot)
   * @return stream of data file paths
   * @throws DataSetLoadException if directory listing fails
   */
  private static Stream<Path> getDataFileStream(final Path directory, final String extension) {
    try (final var files = Files.list(directory)) {
      return files
          .filter(
              path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(extension))
          .toList()
          .stream();
    } catch (final IOException e) {
      throw new DataSetLoadException(
          String.format("Failed to list data files in directory: %s", directory), e);
    }
  }
}
