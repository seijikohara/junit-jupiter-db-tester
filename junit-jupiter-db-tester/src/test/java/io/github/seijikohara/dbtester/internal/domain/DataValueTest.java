package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataValue}. */
@DisplayName("DataValue")
class DataValueTest {

  /** Constructs test instance. */
  DataValueTest() {}

  /** Tests for constructor with non-null value. */
  @Nested
  @DisplayName("Constructor with non-null value")
  class ConstructorWithNonNullValue {

    /** Constructs test instance. */
    ConstructorWithNonNullValue() {}

    /** Verifies that non-null values are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with non-null value")
    void createsInstance_withNonNullValue() {
      // Given
      final var stringValue = "test";

      // When
      final var dataValue = new DataValue(stringValue);

      // Then
      assertNotNull(dataValue);
      assertEquals(stringValue, dataValue.value());
      assertFalse(dataValue.isNull());
    }

    /** Verifies that integer values are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with integer value")
    void createsInstance_withIntegerValue() {
      // Given
      final var intValue = 42;

      // When
      final var dataValue = new DataValue(intValue);

      // Then
      assertEquals(intValue, dataValue.value());
      assertFalse(dataValue.isNull());
    }

    /** Verifies that empty strings are valid non-null values. */
    @Test
    @Tag("edge-case")
    @DisplayName("Creates instance with empty string")
    void createsInstance_withEmptyString() {
      // Given
      final var emptyString = "";

      // When
      final var dataValue = new DataValue(emptyString);

      // Then
      assertEquals(emptyString, dataValue.value());
      assertFalse(dataValue.isNull());
    }
  }

  /** Tests for constructor with null value. */
  @Nested
  @DisplayName("Constructor with null value")
  class ConstructorWithNullValue {

    /** Constructs test instance. */
    ConstructorWithNullValue() {}

    /** Verifies that null values are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with null value")
    void createsInstance_withNullValue() {
      // Given
      final Object nullValue = null;

      // When
      final var dataValue = new DataValue(nullValue);

      // Then
      assertNotNull(dataValue);
      assertNull(dataValue.value());
      assertTrue(dataValue.isNull());
    }
  }

  /** Tests for isNull method. */
  @Nested
  @DisplayName("isNull() method")
  class IsNullMethod {

    /** Constructs test instance. */
    IsNullMethod() {}

    /** Verifies that isNull returns false for non-null values. */
    @Test
    @Tag("normal")
    @DisplayName("Returns false for non-null values")
    void returnsFalse_forNonNullValues() {
      // Given
      final var dataValue = new DataValue("test");

      // When
      final var result = dataValue.isNull();

      // Then
      assertFalse(result);
    }

    /** Verifies that isNull returns true for null values. */
    @Test
    @Tag("normal")
    @DisplayName("Returns true for null values")
    void returnsTrue_forNullValues() {
      // Given
      final var dataValue = new DataValue(null);

      // When
      final var result = dataValue.isNull();

      // Then
      assertTrue(result);
    }

    /** Verifies that isNull returns false for empty string values. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns false for empty string")
    void returnsFalse_forEmptyString() {
      // Given
      final var dataValue = new DataValue("");

      // When
      final var result = dataValue.isNull();

      // Then
      assertFalse(result);
    }
  }

  /** Tests for equals and hashCode methods. */
  @Nested
  @DisplayName("equals() and hashCode() methods")
  class EqualsAndHashCode {

    /** Constructs test instance. */
    EqualsAndHashCode() {}

    /** Verifies that equal non-null values produce equal instances. */
    @Test
    @Tag("normal")
    @DisplayName("Equal non-null values produce equal instances")
    void equalNonNullValues_produceEqualInstances() {
      // Given
      final var dataValue1 = new DataValue("test");
      final var dataValue2 = new DataValue("test");

      // When & Then
      assertEquals(dataValue1, dataValue2);
      assertEquals(dataValue1.hashCode(), dataValue2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var dataValue1 = new DataValue("test1");
      final var dataValue2 = new DataValue("test2");

      // When & Then
      assertNotEquals(dataValue1, dataValue2);
    }

    /** Verifies that two null values are equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Two null values are equal")
    void twoNullValues_areEqual() {
      // Given
      final var dataValue1 = new DataValue(null);
      final var dataValue2 = new DataValue(null);

      // When & Then
      assertEquals(dataValue1, dataValue2);
      assertEquals(dataValue1.hashCode(), dataValue2.hashCode());
    }

    /** Verifies that null and non-null values are not equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Null and non-null values are not equal")
    void nullAndNonNullValues_areNotEqual() {
      // Given
      final var nullValue = new DataValue(null);
      final var nonNullValue = new DataValue("test");

      // When & Then
      assertNotEquals(nullValue, nonNullValue);
    }
  }
}
