package io.github.seijikohara.dbtester.api.exception;

import java.io.Serial;
import java.util.Objects;

/** Raised when a requested {@link javax.sql.DataSource} cannot be located in the registry. */
public final class DataSourceNotFoundException extends DatabaseTesterException {

  /** Serial version UID for serialization compatibility. */
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public DataSourceNotFoundException(final String message) {
    super(Objects.requireNonNull(message, "message must not be null"));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying exception
   */
  public DataSourceNotFoundException(final String message, final Throwable cause) {
    super(
        Objects.requireNonNull(message, "message must not be null"),
        Objects.requireNonNull(cause, "cause must not be null"));
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the underlying exception
   */
  public DataSourceNotFoundException(final Throwable cause) {
    super(Objects.requireNonNull(cause, "cause must not be null"));
  }
}
