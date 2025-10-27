package io.github.seijikohara.dbtester.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Thrown when framework configuration or initialization fails.
 *
 * <p>This exception indicates errors during framework setup, such as failure to instantiate format
 * providers, missing required constructors, or invalid configuration parameters. Unlike {@link
 * DataSetLoadException} which occurs during test execution when loading data files, this exception
 * occurs during framework initialization before any tests run.
 *
 * <p>Common causes include:
 *
 * <ul>
 *   <li>Format provider classes without public no-argument constructors
 *   <li>Abstract or interface classes mistakenly registered as providers
 *   <li>Security restrictions preventing reflective instantiation
 *   <li>Missing dependencies required by provider implementations
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry
 * @see DataSetLoadException
 */
public final class ConfigurationException extends DatabaseTesterException {

  /** Serial version UID for serialization compatibility. */
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ConfigurationException(final String message) {
    super(Objects.requireNonNull(message, "message must not be null"));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying exception
   */
  public ConfigurationException(final String message, final Throwable cause) {
    super(
        Objects.requireNonNull(message, "message must not be null"),
        Objects.requireNonNull(cause, "cause must not be null"));
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the underlying exception
   */
  public ConfigurationException(final Throwable cause) {
    super(Objects.requireNonNull(cause, "cause must not be null"));
  }
}
