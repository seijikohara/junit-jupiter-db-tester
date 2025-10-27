package io.github.seijikohara.dbtester.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Thrown when dataset files cannot be loaded, parsed, or processed during test execution.
 *
 * <p>This exception indicates failures in reading or parsing CSV dataset files. Common causes
 * include:
 *
 * <ul>
 *   <li>Dataset files not found at the expected location
 *   <li>Invalid CSV format or structure
 *   <li>Character encoding issues
 *   <li>Insufficient file system permissions
 *   <li>I/O errors during file reading
 *   <li>Scenario filtering errors when applying scenario markers
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.loader.DataSetLoader
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioDataSet
 */
public final class DataSetLoadException extends DatabaseTesterException {

  /** Serial version UID for serialization compatibility. */
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public DataSetLoadException(final String message) {
    super(Objects.requireNonNull(message, "message must not be null"));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying exception
   */
  public DataSetLoadException(final String message, final Throwable cause) {
    super(
        Objects.requireNonNull(message, "message must not be null"),
        Objects.requireNonNull(cause, "cause must not be null"));
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the underlying exception
   */
  public DataSetLoadException(final Throwable cause) {
    super(Objects.requireNonNull(cause, "cause must not be null"));
  }
}
