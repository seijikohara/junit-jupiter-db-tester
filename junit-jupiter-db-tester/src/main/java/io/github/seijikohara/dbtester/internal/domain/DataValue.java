package io.github.seijikohara.dbtester.internal.domain;

import org.jspecify.annotations.Nullable;

/**
 * Represents a single data value in a database table cell.
 *
 * <p>This value object wraps a nullable database value, providing type safety and explicit null
 * handling for database operations. Database cells can legitimately contain NULL values, so this
 * class allows null as a valid state.
 *
 * <h2>Key Characteristics</h2>
 *
 * <ul>
 *   <li><strong>Nullable by Design:</strong> The value field is explicitly nullable to represent
 *       database NULL values
 *   <li><strong>Immutable:</strong> Once created, the value cannot be changed
 *   <li><strong>Type-Safe:</strong> Provides a domain-specific wrapper around raw Object values
 *   <li><strong>Equality:</strong> Two DataValue instances are equal if their wrapped values are
 *       equal (including both being null)
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Wrapping a non-null value
 * DataValue nameValue = new DataValue("John Doe");
 *
 * // Wrapping a null value (database NULL)
 * DataValue nullValue = new DataValue(null);
 *
 * // Accessing the value
 * Object rawValue = nameValue.value(); // Returns "John Doe"
 * Object nullRawValue = nullValue.value(); // Returns null
 *
 * // Checking for null
 * boolean isNull = nullValue.value() == null; // true
 * }</pre>
 *
 * <h2>Design Rationale</h2>
 *
 * <p>This class exists to provide type safety when working with database values while explicitly
 * acknowledging that NULL is a valid database value. Unlike {@link ColumnName} or {@link TableName}
 * which reject blank values, DataValue accepts null as it represents actual database state.
 *
 * @param value the value to wrap; may be null to represent database NULL
 * @see ColumnName
 * @see TableName
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
