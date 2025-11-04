package io.github.seijikohara.dbtester.api.domain;

/**
 * Identifies a logical scenario within a shared dataset.
 *
 * @param value scenario identifier used for filtering
 */
public record ScenarioName(String value) implements StringIdentifier<ScenarioName> {

  /** Trims and validates the scenario identifier. */
  public ScenarioName {
    value = validateNonBlankString(value, "Scenario name");
  }
}
