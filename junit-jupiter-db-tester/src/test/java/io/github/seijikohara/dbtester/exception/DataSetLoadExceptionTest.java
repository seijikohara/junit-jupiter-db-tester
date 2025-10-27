package io.github.seijikohara.dbtester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSetLoadException}. */
@DisplayName("DataSetLoadException")
class DataSetLoadExceptionTest {

  /** Constructs test instance. */
  DataSetLoadExceptionTest() {}

  /** Verifies that exception is created with message. */
  @Test
  @Tag("normal")
  @DisplayName("Creates exception with message")
  void createsException_withMessage() {
    // Given
    final var message = "Failed to load dataset";

    // When
    final var exception = new DataSetLoadException(message);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  /** Verifies that exception is created with message and cause. */
  @Test
  @Tag("normal")
  @DisplayName("Creates exception with message and cause")
  void createsException_withMessageAndCause() {
    // Given
    final var message = "Failed to load dataset";
    final var cause = new RuntimeException("Underlying cause");

    // When
    final var exception = new DataSetLoadException(message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  /** Verifies that exception is created with cause. */
  @Test
  @Tag("normal")
  @DisplayName("Creates exception with cause")
  void createsException_withCause() {
    // Given
    final var cause = new RuntimeException("Underlying cause");

    // When
    final var exception = new DataSetLoadException(cause);

    // Then
    assertNotNull(exception);
    assertEquals(cause, exception.getCause());
    assertNotNull(exception.getMessage());
  }

  /** Verifies that DataSetLoadException extends DatabaseTesterException. */
  @Test
  @Tag("normal")
  @DisplayName("Extends DatabaseTesterException")
  void extendsDatabaseTesterException() {
    // Given
    final var exception = new DataSetLoadException("Test");

    // When & Then
    assertInstanceOf(DatabaseTesterException.class, exception);
  }
}
