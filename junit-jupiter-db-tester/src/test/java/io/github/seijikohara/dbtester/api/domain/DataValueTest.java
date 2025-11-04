package io.github.seijikohara.dbtester.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataValue}. */
@DisplayName("DataValue")
class DataValueTest {

  /** Tests for the DataValue record. */
  DataValueTest() {}

  /** Tests for the isNull() method. */
  @Nested
  @DisplayName("isNull() method")
  class IsNullMethod {

    /** Tests for the isNull method. */
    IsNullMethod() {}

    /** Verifies that isNull returns true when value is null. */
    @Test
    @Tag("normal")
    @DisplayName("should return true when value is null")
    void shouldReturnTrue_whenValueIsNull() {
      // Given
      final @Nullable Object nullValue = null;
      final var dataValue = new DataValue(nullValue);

      // When
      final var result = dataValue.isNull();

      // Then
      assertTrue(result, "isNull() should return true for null value");
    }

    /** Verifies that isNull returns false when value is not null. */
    @Test
    @Tag("normal")
    @DisplayName("should return false when value is not null")
    void shouldReturnFalse_whenValueIsNotNull() {
      // Given
      final var dataValue = new DataValue("testValue");

      // When
      final var result = dataValue.isNull();

      // Then
      assertFalse(result, "isNull() should return false for non-null value");
    }

    /** Verifies that isNull returns false when value is empty string. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return false when value is empty string")
    void shouldReturnFalse_whenValueIsEmptyString() {
      // Given
      final var dataValue = new DataValue("");

      // When
      final var result = dataValue.isNull();

      // Then
      assertFalse(result, "isNull() should return false for empty string (not null)");
    }

    /** Verifies that isNull returns false when value is zero. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return false when value is zero")
    void shouldReturnFalse_whenValueIsZero() {
      // Given
      final var dataValue = new DataValue(0);

      // When
      final var result = dataValue.isNull();

      // Then
      assertFalse(result, "isNull() should return false for zero (not null)");
    }
  }

  /** Tests for the value() accessor method. */
  @Nested
  @DisplayName("value() method")
  class ValueMethod {

    /** Tests for the value accessor method. */
    ValueMethod() {}

    /** Verifies that value returns null when constructed with null. */
    @Test
    @Tag("normal")
    @DisplayName("should return null when constructed with null")
    void shouldReturnNull_whenConstructedWithNull() {
      // Given
      final @Nullable Object nullValue = null;
      final var dataValue = new DataValue(nullValue);

      // When
      final var result = dataValue.value();

      // Then
      assertNull(result, "value() should return null");
    }

    /** Verifies that value returns the same object when constructed with non-null value. */
    @Test
    @Tag("normal")
    @DisplayName("should return same object when constructed with non-null value")
    void shouldReturnSameObject_whenConstructedWithNonNullValue() {
      // Given
      final var testValue = "testString";
      final var dataValue = new DataValue(testValue);

      // When
      final var result = dataValue.value();

      // Then
      assertEquals(testValue, result, "value() should return the same object");
    }
  }
}
