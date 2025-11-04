package io.github.seijikohara.dbtester.api.domain;

/**
 * Wrapper for the column name that carries scenario metadata in scenario-aware datasets.
 *
 * @param value marker column identifier
 */
public record ScenarioMarker(String value) implements StringIdentifier<ScenarioMarker> {

  /** Trims and validates the marker identifier. */
  public ScenarioMarker {
    value = validateNonBlankString(value, "Scenario marker");
  }
}
