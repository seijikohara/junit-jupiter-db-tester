package io.github.seijikohara.dbtester.api.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ConventionSettings}. */
@DisplayName("ConventionSettings")
class ConventionSettingsTest {

  /** Tests for the ConventionSettings record. */
  ConventionSettingsTest() {}

  /** Tests for the ConventionSettings constructor. */
  @Nested
  @DisplayName("constructor")
  class ConstructorMethod {

    /** Tests for the constructor. */
    ConstructorMethod() {}

    /** Verifies that constructor creates instance with all valid parameters. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance when all parameters are valid")
    void shouldCreateInstance_whenAllParametersAreValid() {
      // Given
      final var baseDir = "/test/data";
      final var suffix = "/verify";
      final var marker = "[Test]";

      // When
      final var settings = new ConventionSettings(baseDir, suffix, marker);

      // Then
      assertAll(
          "convention settings components",
          () -> assertEquals(baseDir, settings.baseDirectory()),
          () -> assertEquals(suffix, settings.expectationSuffix()),
          () -> assertEquals(marker, settings.scenarioMarker()));
    }

    /** Verifies that constructor creates instance when baseDirectory is null. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance when baseDirectory is null")
    void shouldCreateInstance_whenBaseDirectoryIsNull() {
      // Given
      final @Nullable String nullBaseDir = null;
      final var suffix = "/expected";
      final var marker = "[Scenario]";

      // When
      final var settings = new ConventionSettings(nullBaseDir, suffix, marker);

      // Then
      assertAll(
          "convention settings with null baseDirectory",
          () -> assertNull(settings.baseDirectory()),
          () -> assertEquals(suffix, settings.expectationSuffix()),
          () -> assertEquals(marker, settings.scenarioMarker()));
    }

    /** Verifies that constructor throws exception when expectationSuffix is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when expectationSuffix is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenExpectationSuffixIsNull() {
      // Given
      final @Nullable String nullSuffix = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> new ConventionSettings(null, nullSuffix, "[S]"));

      assertEquals("expectationSuffix must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when scenarioMarker is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when scenarioMarker is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenScenarioMarkerIsNull() {
      // Given
      final @Nullable String nullMarker = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new ConventionSettings(null, "/expected", nullMarker));

      assertEquals("scenarioMarker must not be null", exception.getMessage());
    }
  }

  /** Tests for the standard() factory method. */
  @Nested
  @DisplayName("standard() method")
  class StandardMethod {

    /** Tests for the standard method. */
    StandardMethod() {}

    /** Verifies that standard method returns settings with default values. */
    @Test
    @Tag("normal")
    @DisplayName("should return settings with default values")
    void shouldReturnSettings_withDefaultValues() {
      // When
      final var settings = ConventionSettings.standard();

      // Then
      assertAll(
          "standard convention settings",
          () -> assertNull(settings.baseDirectory(), "baseDirectory should be null"),
          () -> assertEquals("/expected", settings.expectationSuffix()),
          () -> assertEquals("[Scenario]", settings.scenarioMarker()));
    }
  }

  /** Tests for record equality and hash code. */
  @Nested
  @DisplayName("equals() and hashCode() methods")
  class EqualsAndHashCodeMethods {

    /** Tests for equals and hashCode methods. */
    EqualsAndHashCodeMethods() {}

    /** Verifies that two instances with same values are equal. */
    @Test
    @Tag("normal")
    @DisplayName("should be equal when instances have same values")
    void shouldBeEqual_whenInstancesHaveSameValues() {
      // Given
      final var settings1 = new ConventionSettings("/data", "/verify", "[Test]");
      final var settings2 = new ConventionSettings("/data", "/verify", "[Test]");

      // When & Then
      assertAll(
          "equality",
          () -> assertEquals(settings1, settings2),
          () -> assertEquals(settings1.hashCode(), settings2.hashCode()));
    }

    /** Verifies that two instances with different values are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when instances have different values")
    void shouldNotBeEqual_whenInstancesHaveDifferentValues() {
      // Given
      final var settings1 = new ConventionSettings("/data1", "/verify", "[Test]");
      final var settings2 = new ConventionSettings("/data2", "/verify", "[Test]");

      // When & Then
      assertNotEquals(settings1, settings2);
    }

    /** Verifies that instances with different expectationSuffix are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when expectationSuffix differs")
    void shouldNotBeEqual_whenExpectationSuffixDiffers() {
      // Given
      final var settings1 = new ConventionSettings(null, "/expected", "[Scenario]");
      final var settings2 = new ConventionSettings(null, "/verify", "[Scenario]");

      // When & Then
      assertNotEquals(settings1, settings2);
    }

    /** Verifies that instances with different scenarioMarker are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when scenarioMarker differs")
    void shouldNotBeEqual_whenScenarioMarkerDiffers() {
      // Given
      final var settings1 = new ConventionSettings(null, "/expected", "[Scenario]");
      final var settings2 = new ConventionSettings(null, "/expected", "[Test]");

      // When & Then
      assertNotEquals(settings1, settings2);
    }
  }
}
