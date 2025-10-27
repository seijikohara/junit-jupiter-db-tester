package io.github.seijikohara.dbtester.internal.bridge.dbunit.format;

import io.github.seijikohara.dbtester.internal.dataset.DataSet;
import java.nio.file.Path;

/**
 * Strategy interface for format-specific dataset readers.
 *
 * <p>This interface defines the contract for reading datasets from files in various formats (CSV,
 * JSON, YAML, XML, etc.). Each implementation handles a specific file format, allowing the
 * framework to support multiple formats without modifying core logic.
 *
 * <h2>Design Pattern</h2>
 *
 * <p><strong>Strategy Pattern:</strong> Different file format parsers implement this interface,
 * allowing runtime selection of the appropriate reader based on file format.
 *
 * <h2>Current Implementations</h2>
 *
 * <ul>
 *   <li>{@link CsvDataSetReader} - CSV file format
 * </ul>
 *
 * <h2>Future Implementations</h2>
 *
 * <ul>
 *   <li>JsonDataSetReader - JSON file format
 *   <li>YamlDataSetReader - YAML file format
 *   <li>XmlDataSetReader - XML file format
 * </ul>
 *
 * <h2>Usage Pattern</h2>
 *
 * <pre>{@code
 * // Select reader based on format
 * final DataSetReader reader = new CsvDataSetReader();
 * final var dataSet = reader.read(directory);
 * }</pre>
 *
 * <h2>Null Safety</h2>
 *
 * <p>This is an INTERNAL interface (package-private). NullAway enforces null safety at compile
 * time; no runtime {@code Objects.requireNonNull()} checks are needed.
 *
 * @see CsvDataSetReader
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge
 */
public interface DataSetReader {

  /**
   * Reads dataset from the specified directory or file.
   *
   * <p>The exact interpretation of the path depends on the format:
   *
   * <ul>
   *   <li>CSV: Directory containing one CSV file per table
   *   <li>JSON/YAML/XML: Single file containing all tables
   * </ul>
   *
   * @param path the directory or file containing dataset
   * @return the loaded dataset
   * @throws io.github.seijikohara.dbtester.exception.DataSetLoadException if loading fails
   */
  DataSet read(Path path);
}
