package io.github.seijikohara.dbtester.config;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.api.operation.Operation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link OperationDefaults}. */
@DisplayName("OperationDefaults")
class OperationDefaultsTest {

  /** Constructs test instance. */
  OperationDefaultsTest() {}

  /** Tests for standard factory method. */
  @Nested
  @DisplayName("standard() method")
  class StandardMethod {

    /** Constructs test instance. */
    StandardMethod() {}

    /** Verifies that standard defaults are created with expected values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates defaults with standard values")
    void createsDefaults_withStandardValues() {
      // When
      final var defaults = OperationDefaults.standard();

      // Then
      assertNotNull(defaults);
      assertEquals(Operation.CLEAN_INSERT, defaults.preparation());
      assertEquals(Operation.NONE, defaults.expectation());
    }
  }

  /** Tests for constructor with custom values. */
  @Nested
  @DisplayName("Constructor with custom values")
  class ConstructorWithCustomValues {

    /** Constructs test instance. */
    ConstructorWithCustomValues() {}

    /** Verifies that defaults are created with custom values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates defaults with custom values")
    void createsDefaults_withCustomValues() {
      // Given
      final var preparation = Operation.INSERT;
      final var expectation = Operation.UPDATE;

      // When
      final var defaults = new OperationDefaults(preparation, expectation);

      // Then
      assertNotNull(defaults);
      assertEquals(preparation, defaults.preparation());
      assertEquals(expectation, defaults.expectation());
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
      final var defaults1 = OperationDefaults.standard();
      final var defaults2 = OperationDefaults.standard();

      // When & Then
      assertEquals(defaults1, defaults2);
      assertEquals(defaults1.hashCode(), defaults2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var defaults1 = OperationDefaults.standard();
      final var defaults2 = new OperationDefaults(Operation.INSERT, Operation.UPDATE);

      // When & Then
      assertNotEquals(defaults1, defaults2);
    }
  }
}
