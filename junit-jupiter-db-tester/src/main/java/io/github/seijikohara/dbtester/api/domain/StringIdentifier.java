package io.github.seijikohara.dbtester.api.domain;

import java.util.Objects;

/**
 * Sealed interface for string-based domain identifiers that support natural ordering.
 *
 * <p>This interface provides a common contract for domain identifiers that:
 *
 * <ul>
 *   <li>Are backed by a non-null, non-blank string value
 *   <li>Are immutable and type-safe
 *   <li>Support natural ordering through {@link Comparable} implementation
 *   <li>Provide validation logic for consistent error handling
 * </ul>
 *
 * <p>Only specific domain identifier records are permitted to implement this interface, ensuring
 * type safety and preventing uncontrolled extension.
 *
 * <h2>Type Parameter and Self-Typing</h2>
 *
 * <p>This interface uses the <em>Curiously Recurring Template Pattern</em> (also known as F-bounded
 * polymorphism) to ensure type-safe comparisons. Each implementing record declares itself as the
 * type parameter:
 *
 * <pre>{@code
 * public record ColumnName(String value) implements StringIdentifier<ColumnName> {
 *   public ColumnName {
 *     value = StringIdentifier.validateNonBlankString(value, "Column name");
 *   }
 * }
 * }</pre>
 *
 * <h2>Validation</h2>
 *
 * <p>The {@link #validateNonBlankString(String, String)} method provides consistent validation
 * logic that all implementing records should use in their compact constructors. This ensures
 * uniform error messages and validation behavior across all identifier types.
 *
 * <h2>Comparison Contract</h2>
 *
 * <p>All identifiers are compared lexicographically by their string {@link #value()}. The default
 * {@link #compareTo(StringIdentifier)} implementation provides this logic automatically, so
 * implementing records do not need to override {@code compareTo()}.
 *
 * <p>Type-safe comparisons are enforced at compile time: {@code ColumnName} can only be compared
 * with {@code ColumnName}, {@code TableName} with {@code TableName}, etc. Attempting to compare
 * different identifier types (e.g., {@code columnName.compareTo(tableName)}) results in a
 * compile-time error.
 *
 * @param <T> the concrete identifier type implementing this interface (self-type)
 */
public sealed interface StringIdentifier<T extends StringIdentifier<T>> extends Comparable<T>
    permits ColumnName, DataSourceName, ScenarioMarker, SchemaName, ScenarioName, TableName {

  /**
   * Returns the string value of this identifier.
   *
   * <p>The value is guaranteed to be:
   *
   * <ul>
   *   <li>Non-null
   *   <li>Non-blank (not empty after trimming)
   *   <li>Trimmed (no leading or trailing whitespace)
   * </ul>
   *
   * @return the string value of this identifier
   */
  String value();

  /**
   * Compares this identifier with another for natural ordering.
   *
   * <p>Identifiers are ordered lexicographically by their string {@link #value()}. This default
   * implementation is inherited by all implementing records, eliminating the need for each record
   * to provide its own {@code compareTo()} implementation.
   *
   * @param other the other identifier to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  default int compareTo(final T other) {
    return this.value().compareTo(other.value());
  }

  /**
   * Validates and normalizes a non-null, non-blank string identifier.
   *
   * <p>Performs three validation steps:
   *
   * <ol>
   *   <li>Null check - throws NullPointerException if value is null
   *   <li>Trimming - removes leading and trailing whitespace
   *   <li>Blank check - throws IllegalArgumentException if trimmed value is blank
   * </ol>
   *
   * <p>All implementing records should call this method in their compact constructor to ensure
   * consistent validation:
   *
   * <pre>{@code
   * public record ColumnName(String value) implements StringIdentifier<ColumnName> {
   *   public ColumnName {
   *     value = StringIdentifier.validateNonBlankString(value, "Column name");
   *   }
   * }
   * }</pre>
   *
   * @param value the raw string value to validate
   * @param paramName the parameter name used in error messages (e.g., "Column name", "Table name")
   * @return the trimmed, validated string value
   * @throws NullPointerException if value is null
   * @throws IllegalArgumentException if value is blank after trimming
   */
  default String validateNonBlankString(final String value, final String paramName) {
    Objects.requireNonNull(value, String.format("%s must not be null", paramName));
    final var trimmed = value.trim();
    if (trimmed.isBlank()) {
      throw new IllegalArgumentException(String.format("%s must not be blank", paramName));
    }
    return trimmed;
  }
}
