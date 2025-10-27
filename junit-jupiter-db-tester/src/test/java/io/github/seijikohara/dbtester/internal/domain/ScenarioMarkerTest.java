package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ScenarioMarker}. */
@DisplayName("ScenarioMarker")
class ScenarioMarkerTest {

  /** Constructs test instance. */
  ScenarioMarkerTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid scenario markers are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid marker")
    void createsInstance_withValidMarker() {
      // Given
      final var validMarker = "#scenario";

      // When
      final var scenarioMarker = new ScenarioMarker(validMarker);

      // Then
      assertNotNull(scenarioMarker);
      assertEquals(validMarker, scenarioMarker.value());
    }

    /** Verifies that custom scenario markers are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with custom marker")
    void createsInstance_withCustomMarker() {
      // Given
      final var customMarker = "[Scenario]";

      // When
      final var scenarioMarker = new ScenarioMarker(customMarker);

      // Then
      assertEquals(customMarker, scenarioMarker.value());
    }

    /** Verifies that scenario markers are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var markerWithSpaces = "  #scenario  ";

      // When
      final var scenarioMarker = new ScenarioMarker(markerWithSpaces);

      // Then
      assertEquals("#scenario", scenarioMarker.value());
    }

    /** Verifies that empty string is rejected. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when marker is empty")
    void throwsException_whenMarkerIsEmpty() {
      // Given
      final var emptyMarker = "";

      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new ScenarioMarker(emptyMarker));
      assertEquals("Scenario marker must not be blank", exception.getMessage());
    }

    /** Verifies that blank string is rejected. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when marker is blank")
    void throwsException_whenMarkerIsBlank() {
      // Given
      final var blankMarker = "   ";

      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new ScenarioMarker(blankMarker));
      assertEquals("Scenario marker must not be blank", exception.getMessage());
    }
  }

  /** Tests for equals and hashCode methods. */
  @Nested
  @DisplayName("equals() and hashCode() methods")
  class EqualsAndHashCode {

    /** Constructs test instance. */
    EqualsAndHashCode() {}

    /** Verifies that equal values produce equal instances. */
    @Test
    @Tag("normal")
    @DisplayName("Equal values produce equal instances")
    void equalValues_produceEqualInstances() {
      // Given
      final var scenarioMarker1 = new ScenarioMarker("#scenario");
      final var scenarioMarker2 = new ScenarioMarker("#scenario");

      // When & Then
      assertEquals(scenarioMarker1, scenarioMarker2);
      assertEquals(scenarioMarker1.hashCode(), scenarioMarker2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var marker1 = new ScenarioMarker("#scenario");
      final var marker2 = new ScenarioMarker("[Scenario]");

      // When & Then
      assertNotEquals(marker1, marker2);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var scenarioMarker1 = new ScenarioMarker("#scenario");
      final var scenarioMarker2 = new ScenarioMarker("  #scenario  ");

      // When & Then
      assertEquals(scenarioMarker1, scenarioMarker2);
      assertEquals(scenarioMarker1.hashCode(), scenarioMarker2.hashCode());
    }
  }
}
