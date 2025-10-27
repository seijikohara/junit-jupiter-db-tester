package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a database table name.
 *
 * <p>This type provides type safety for table identifiers, preventing confusion with column names,
 * scenario names, or other string-based identifiers. It enforces basic validation rules for table
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
 * TableName tableName = new TableName("USERS");
 * String rawName = tableName.value();  // "USERS"
 * }</pre>
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the table name string value
 *   <li><strong>Natural Ordering:</strong> Alphabetically ordered by table name value
 * </ul>
 *
 * @param value the table name string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 */
public record TableName(String value) implements Comparable<TableName> {

  /** Compact constructor with validation and normalization. */
  public TableName {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Table name must not be blank");
    }
  }

  /**
   * Compares this table name with another for natural ordering.
   *
   * <p>Table names are ordered alphabetically by their string value.
   *
   * @param other the other table name to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  public int compareTo(final TableName other) {
    return this.value.compareTo(other.value);
  }
}
