package io.github.seijikohara.dbtester.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Base unchecked exception for all database testing framework errors.
 *
 * <p>This is the root exception class for the framework. All specific exceptions extend this base
 * class, allowing clients to catch all framework-related errors with a single catch block if
 * desired. Specific exception subclasses include:
 *
 * <ul>
 *   <li>{@link ConfigurationException} - Framework initialization and configuration failures
 *   <li>{@link DataSetLoadException} - Dataset file loading and parsing failures
 *   <li>{@link DataSourceNotFoundException} - Missing or unregistered data source
 *   <li>{@link ValidationException} - Database assertion and expectation validation failures
 * </ul>
 *
 * @see ConfigurationException
 * @see DataSetLoadException
 * @see DataSourceNotFoundException
 * @see ValidationException
 */
public class DatabaseTesterException extends RuntimeException {

  /** Serial version UID for serialization compatibility. */
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public DatabaseTesterException(final String message) {
    super(Objects.requireNonNull(message, "message must not be null"));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying exception
   */
  public DatabaseTesterException(final String message, final Throwable cause) {
    super(
        Objects.requireNonNull(message, "message must not be null"),
        Objects.requireNonNull(cause, "cause must not be null"));
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the underlying exception
   */
  public DatabaseTesterException(final Throwable cause) {
    super(Objects.requireNonNull(cause, "cause must not be null"));
  }
}
