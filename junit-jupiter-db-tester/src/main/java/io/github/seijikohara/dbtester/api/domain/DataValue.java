package io.github.seijikohara.dbtester.api.domain;

import org.jspecify.annotations.Nullable;

/**
 * Wrapper for a cell value that may legitimately be {@code null}.
 *
 * @param value underlying cell value; may be {@code null}
 */
public record DataValue(@Nullable Object value) {
  /** Compact constructor with no validation (null is a valid database value). */
  public DataValue {}

  /**
   * Checks if this DataValue represents a NULL database value.
   *
   * @return true if the wrapped value is null, false otherwise
   */
  public boolean isNull() {
    return value == null;
  }
}
