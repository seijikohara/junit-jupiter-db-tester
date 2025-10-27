package io.github.seijikohara.dbtester.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Thrown when database assertion or validation fails during expectation verification.
 *
 * <p>Indicates that the actual database state does not match the expected state. Common causes
 * include row count mismatches, column value mismatches, missing or extra rows, or null value
 * mismatches. To validate only specific columns, include only those columns in your expectation CSV
 * files.
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge
 * @see io.github.seijikohara.dbtester.api.assertion.DatabaseAssertion
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 */
public final class ValidationException extends DatabaseTesterException {

  /** Serial version UID for serialization compatibility. */
  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ValidationException(final String message) {
    super(Objects.requireNonNull(message, "message must not be null"));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying exception
   */
  public ValidationException(final String message, final Throwable cause) {
    super(
        Objects.requireNonNull(message, "message must not be null"),
        Objects.requireNonNull(cause, "cause must not be null"));
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the underlying exception
   */
  public ValidationException(final Throwable cause) {
    super(Objects.requireNonNull(cause, "cause must not be null"));
  }
}
