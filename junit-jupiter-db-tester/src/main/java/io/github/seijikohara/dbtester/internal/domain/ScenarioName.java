package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a test scenario name.
 *
 * <p>This type provides type safety for scenario identifiers used in CSV-based test data filtering.
 * It prevents confusion with table names, column names, or other string-based identifiers.
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
 * ScenarioName scenarioName = new ScenarioName("testCreateUser");
 * String rawName = scenarioName.value();  // "testCreateUser"
 * }</pre>
 *
 * <h2>Context</h2>
 *
 * <p>Scenario names are used in the scenario marker column (see {@link
 * io.github.seijikohara.dbtester.config.ConventionSettings#scenarioMarker()}) of CSV files to
 * filter rows for specific test cases. Multiple tests can share the same CSV file by using
 * different scenario names.
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the scenario name string value
 * </ul>
 *
 * @param value the scenario name string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioTable
 */
public record ScenarioName(String value) implements Comparable<ScenarioName> {

  /** Compact constructor with validation and normalization. */
  public ScenarioName {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Scenario name must not be blank");
    }
  }

  /**
   * Compares this scenario name with another for natural ordering.
   *
   * <p>ScenarioNames are ordered alphabetically by their string value.
   *
   * @param other the other scenario name to compare to
   * @return negative if this &lt; other, zero if equal, positive if this &gt; other
   */
  @Override
  public int compareTo(final ScenarioName other) {
    return this.value.compareTo(other.value);
  }
}
