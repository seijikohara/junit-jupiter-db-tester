package io.github.seijikohara.dbtester.api.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.seijikohara.dbtester.api.operation.Operation;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link OperationDefaults}. */
@DisplayName("OperationDefaults")
class OperationDefaultsTest {

  /** Tests for the OperationDefaults record. */
  OperationDefaultsTest() {}

  /** Tests for the OperationDefaults constructor. */
  @Nested
  @DisplayName("constructor")
  class ConstructorMethod {

    /** Tests for the constructor. */
    ConstructorMethod() {}

    /** Verifies that constructor creates instance with valid parameters. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance when all parameters are valid")
    void shouldCreateInstance_whenAllParametersAreValid() {
      // Given
      final var preparation = Operation.INSERT;
      final var expectation = Operation.UPDATE;

      // When
      final var defaults = new OperationDefaults(preparation, expectation);

      // Then
      assertAll(
          "operation defaults components",
          () -> assertEquals(preparation, defaults.preparation()),
          () -> assertEquals(expectation, defaults.expectation()));
    }

    /** Verifies that constructor throws exception when preparation is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when preparation is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenPreparationIsNull() {
      // Given
      final @Nullable Operation nullPreparation = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new OperationDefaults(nullPreparation, Operation.NONE));

      assertEquals("preparation must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when expectation is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when expectation is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenExpectationIsNull() {
      // Given
      final @Nullable Operation nullExpectation = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new OperationDefaults(Operation.CLEAN_INSERT, nullExpectation));

      assertEquals("expectation must not be null", exception.getMessage());
    }
  }

  /** Tests for the standard() factory method. */
  @Nested
  @DisplayName("standard() method")
  class StandardMethod {

    /** Tests for the standard method. */
    StandardMethod() {}

    /** Verifies that standard method returns defaults with standard values. */
    @Test
    @Tag("normal")
    @DisplayName("should return defaults with standard values")
    void shouldReturnDefaults_withStandardValues() {
      // When
      final var defaults = OperationDefaults.standard();

      // Then
      assertAll(
          "standard operation defaults",
          () -> assertEquals(Operation.CLEAN_INSERT, defaults.preparation()),
          () -> assertEquals(Operation.NONE, defaults.expectation()));
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
      final var defaults1 = new OperationDefaults(Operation.INSERT, Operation.UPDATE);
      final var defaults2 = new OperationDefaults(Operation.INSERT, Operation.UPDATE);

      // When & Then
      assertAll(
          "equality",
          () -> assertEquals(defaults1, defaults2),
          () -> assertEquals(defaults1.hashCode(), defaults2.hashCode()));
    }

    /** Verifies that two instances with different preparation are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when preparation differs")
    void shouldNotBeEqual_whenPreparationDiffers() {
      // Given
      final var defaults1 = new OperationDefaults(Operation.INSERT, Operation.NONE);
      final var defaults2 = new OperationDefaults(Operation.UPDATE, Operation.NONE);

      // When & Then
      assertNotEquals(defaults1, defaults2);
    }

    /** Verifies that two instances with different expectation are not equal. */
    @Test
    @Tag("normal")
    @DisplayName("should not be equal when expectation differs")
    void shouldNotBeEqual_whenExpectationDiffers() {
      // Given
      final var defaults1 = new OperationDefaults(Operation.CLEAN_INSERT, Operation.NONE);
      final var defaults2 = new OperationDefaults(Operation.CLEAN_INSERT, Operation.UPDATE);

      // When & Then
      assertNotEquals(defaults1, defaults2);
    }
  }
}
