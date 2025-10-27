package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a database column name.
 *
 * <p>This type provides type safety for column identifiers, preventing confusion with table names,
 * scenario names, or other string-based identifiers. It enforces basic validation rules for column
 * names.
 *
 * <h2>Validation Rules</h2>
 *
 * <ul>
 *   <li>Must not be blank (empty or whitespace-only)
 *   <li>Leading and trailing whitespace is trimmed
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * ColumnName columnName = new ColumnName("USER_ID");
 * String rawName = columnName.value();  // "USER_ID"
 * }</pre>
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the column name string value
 *   <li><strong>Natural Ordering:</strong> Alphabetically ordered by column name value
 * </ul>
 *
 * @param value the column name string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 */
public record ColumnName(String value) implements Comparable<ColumnName> {

  /** Compact constructor with validation and normalization. */
  public ColumnName {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Column name must not be blank");
    }
  }

  /**
   * Compares this column name with another for natural ordering.
   *
   * <p>Column names are ordered alphabetically by their string value.
   *
   * @param other the other column name to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  public int compareTo(final ColumnName other) {
    return this.value.compareTo(other.value);
  }
}
