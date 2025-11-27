package io.github.seijikohara.dbtester.spring.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Unit tests for {@link DatabaseTesterProperties}. */
@DisplayName("DatabaseTesterProperties")
class DatabaseTesterPropertiesTest {

  /** Creates a new test instance. */
  DatabaseTesterPropertiesTest() {}

  /** Tests for DatabaseTesterProperties record construction. */
  @Nested
  @DisplayName("record construction")
  class RecordConstruction {

    /** Tests for DatabaseTesterProperties record construction. */
    RecordConstruction() {}

    /**
     * Verifies that record can be created with various flag combinations.
     *
     * @param enabled the enabled flag value
     * @param autoRegister the autoRegisterDataSources flag value
     */
    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    @Tag("normal")
    @DisplayName("should create instance with various flag combinations")
    void shouldCreateInstance_withVariousFlagCombinations(
        final boolean enabled, final boolean autoRegister) {
      final var properties = new DatabaseTesterProperties(enabled, autoRegister);

      assertAll(
          "properties values",
          () -> assertEquals(enabled, properties.enabled()),
          () -> assertEquals(autoRegister, properties.autoRegisterDataSources()));
    }
  }

  /** Tests for DatabaseTesterProperties record equality. */
  @Nested
  @DisplayName("record equality")
  class RecordEquality {

    /** Tests for DatabaseTesterProperties record equality. */
    RecordEquality() {}

    /** Verifies that records with same values are equal. */
    @Test
    @Tag("normal")
    @DisplayName("should be equal when values are the same")
    void shouldBeEqual_whenValuesAreSame() {
      final var properties1 = new DatabaseTesterProperties(true, true);
      final var properties2 = new DatabaseTesterProperties(true, true);

      assertAll(
          "equality and hashCode",
          () -> assertEquals(properties1, properties2),
          () -> assertEquals(properties1.hashCode(), properties2.hashCode()));
    }

    /** Verifies that records with different values are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when values differ")
    void shouldNotBeEqual_whenValuesDiffer() {
      final var properties1 = new DatabaseTesterProperties(true, true);
      final var properties2 = new DatabaseTesterProperties(true, false);

      assertNotEquals(properties1, properties2);
    }
  }

  /** Tests for DatabaseTesterProperties toString method. */
  @Nested
  @DisplayName("toString method")
  class ToStringMethod {

    /** Tests for DatabaseTesterProperties toString method. */
    ToStringMethod() {}

    /** Verifies that toString returns readable string representation. */
    @Test
    @Tag("normal")
    @DisplayName("should return readable string representation")
    void shouldReturnReadableStringRepresentation() {
      final var properties = new DatabaseTesterProperties(true, false);

      final var result = properties.toString();

      assertAll(
          "toString content",
          () -> assertTrue(result.contains("enabled=true")),
          () -> assertTrue(result.contains("autoRegisterDataSources=false")));
    }
  }
}
