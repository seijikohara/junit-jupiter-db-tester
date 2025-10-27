package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a database schema name.
 *
 * <p>This type provides type safety for schema identifiers, preventing confusion with table names,
 * column names, or other string-based identifiers. It enforces basic validation rules for schema
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
 * SchemaName schemaName = new SchemaName("public");
 * String rawName = schemaName.value();  // "public"
 * }</pre>
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the schema name string value
 *   <li><strong>Natural Ordering:</strong> Alphabetically ordered by schema name value
 * </ul>
 *
 * @param value the schema name string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 */
public record SchemaName(String value) implements Comparable<SchemaName> {

  /** Compact constructor with validation and normalization. */
  public SchemaName {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Schema name must not be blank");
    }
  }

  /**
   * Compares this schema name with another for natural ordering.
   *
   * <p>Schema names are ordered alphabetically by their string value.
   *
   * @param other the other schema name to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  public int compareTo(final SchemaName other) {
    return this.value.compareTo(other.value);
  }
}
