package io.github.seijikohara.dbtester.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Thrown when a required data source is not registered in the data source registry.
 *
 * <p>Data sources must be registered with {@link
 * io.github.seijikohara.dbtester.config.DataSourceRegistry} before test execution, typically in a
 * {@code @BeforeAll} setup method.
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * @BeforeAll
 * static void setup() {
 *   DataSourceRegistry registry = new DataSourceRegistry();
 *   registry.registerDefault(dataSource);
 *   // or register named data source
 *   registry.register("primary", dataSource);
 * }
 * }</pre>
 *
 * @see io.github.seijikohara.dbtester.config.DataSourceRegistry
 */
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
