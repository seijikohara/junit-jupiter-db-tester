package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a data source name.
 *
 * <p>This type provides type safety for data source identifiers, preventing confusion with table
 * names, column names, or other string-based identifiers. It is used to identify registered JDBC
 * data sources in the framework.
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
 * DataSourceName defaultName = new DataSourceName("default");
 * DataSourceName customName = new DataSourceName("reporting-db");
 * String rawName = customName.value();  // "reporting-db"
 * }</pre>
 *
 * <h2>Context</h2>
 *
 * <p>Data source names are used to identify and retrieve registered JDBC data sources from {@link
 * io.github.seijikohara.dbtester.config.DataSourceRegistry}. Tests can specify which data source to
 * use via the {@code dataSource} attribute in {@code @DataSet} annotations.
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the data source name string value
 * </ul>
 *
 * @param value the data source name string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 * @see io.github.seijikohara.dbtester.config.DataSourceRegistry
 */
public record DataSourceName(String value) implements Comparable<DataSourceName> {

  /** Compact constructor with validation and normalization. */
  public DataSourceName {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Data source name must not be blank");
    }
  }

  /**
   * Compares this data source name with another for natural ordering.
   *
   * <p>DataSourceNames are ordered alphabetically by their string value.
   *
   * @param other the other data source name to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  public int compareTo(final DataSourceName other) {
    return this.value.compareTo(other.value);
  }
}
