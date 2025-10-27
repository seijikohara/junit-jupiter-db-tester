package io.github.seijikohara.dbtester.internal.domain;

/**
 * Immutable value object representing a scenario marker column identifier.
 *
 * <p>This type provides type safety for the special column name used to identify scenario-specific
 * rows in test data files. It prevents confusion with regular column names, table names, or other
 * string-based identifiers.
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
 * ScenarioMarker marker = new ScenarioMarker("#scenario");
 * String rawMarker = marker.value();  // "#scenario"
 * }</pre>
 *
 * <h2>Context</h2>
 *
 * <p>The scenario marker identifies a special column in CSV files that contains scenario names for
 * row filtering. When loading test data, only rows matching the specified scenario names are
 * included. This allows multiple test cases to share the same data files.
 *
 * <p>By convention, the default marker is {@code "#scenario"}, but it can be customized via {@link
 * io.github.seijikohara.dbtester.config.ConventionSettings#scenarioMarker()}.
 *
 * <h2>Design Notes</h2>
 *
 * <ul>
 *   <li><strong>Internal Package:</strong> No runtime null checks - NullAway enforces compile-time
 *       safety
 *   <li><strong>Immutable:</strong> Thread-safe by design
 *   <li><strong>Value Semantics:</strong> Equality based on the marker string value
 * </ul>
 *
 * @param value the scenario marker string (non-blank after trimming); throws {@code
 *     IllegalArgumentException} if blank
 * @see ScenarioName
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioTable
 * @see io.github.seijikohara.dbtester.config.ConventionSettings#scenarioMarker()
 */
public record ScenarioMarker(String value) {

  /** Compact constructor with validation and normalization. */
  public ScenarioMarker {
    value = value.trim();
    if (value.isBlank()) {
      throw new IllegalArgumentException("Scenario marker must not be blank");
    }
  }
}
