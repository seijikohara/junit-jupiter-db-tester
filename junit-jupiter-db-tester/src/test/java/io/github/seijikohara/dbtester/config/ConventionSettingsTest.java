package io.github.seijikohara.dbtester.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ConventionSettings}. */
@DisplayName("ConventionSettings")
class ConventionSettingsTest {

  /** Constructs test instance. */
  ConventionSettingsTest() {}

  /** Tests for standard factory method. */
  @Nested
  @DisplayName("standard() method")
  class StandardMethod {

    /** Constructs test instance. */
    StandardMethod() {}

    /** Verifies that standard settings are created with default values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates settings with default values")
    void createsSettings_withDefaultValues() {
      // When
      final var settings = ConventionSettings.standard();

      // Then
      assertNotNull(settings);
      assertNull(settings.baseDirectory());
      assertEquals("/expected", settings.expectationSuffix());
      assertEquals("[Scenario]", settings.scenarioMarker());
    }
  }

  /** Tests for constructor with custom values. */
  @Nested
  @DisplayName("Constructor with custom values")
  class ConstructorWithCustomValues {

    /** Constructs test instance. */
    ConstructorWithCustomValues() {}

    /** Verifies that settings are created with custom values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates settings with custom values")
    void createsSettings_withCustomValues() {
      // Given
      final var baseDir = "test-data";
      final var expectationSuffix = "/expected-data";
      final var scenarioMarker = "[Test]";

      // When
      final var settings = new ConventionSettings(baseDir, expectationSuffix, scenarioMarker);

      // Then
      assertNotNull(settings);
      assertEquals(baseDir, settings.baseDirectory());
      assertEquals(expectationSuffix, settings.expectationSuffix());
      assertEquals(scenarioMarker, settings.scenarioMarker());
    }

    /** Verifies that baseDirectory can be null. */
    @Test
    @Tag("edge-case")
    @DisplayName("Allows null baseDirectory")
    void allowsNull_baseDirectory() {
      // Given
      final String nullBaseDir = null;

      // When
      final var settings = new ConventionSettings(nullBaseDir, "/expected", "[Scenario]");

      // Then
      assertNull(settings.baseDirectory());
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
      final var settings1 = ConventionSettings.standard();
      final var settings2 = ConventionSettings.standard();

      // When & Then
      assertEquals(settings1, settings2);
      assertEquals(settings1.hashCode(), settings2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var settings1 = ConventionSettings.standard();
      final var settings2 = new ConventionSettings("custom", "/expected", "[Scenario]");

      // When & Then
      assertNotEquals(settings1, settings2);
    }
  }
}
