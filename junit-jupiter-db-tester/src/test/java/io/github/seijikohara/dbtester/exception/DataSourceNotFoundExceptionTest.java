package io.github.seijikohara.dbtester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSourceNotFoundException}. */
@DisplayName("DataSourceNotFoundException")
class DataSourceNotFoundExceptionTest {

  /** Constructs test instance. */
  DataSourceNotFoundExceptionTest() {}

  /** Verifies that exception is created with message. */
  @Test
  @Tag("normal")
  @DisplayName("Creates exception with message")
  void createsException_withMessage() {
    // Given
    final var message = "DataSource not found";

    // When
    final var exception = new DataSourceNotFoundException(message);

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
    final var message = "DataSource not found";
    final var cause = new RuntimeException("Underlying cause");

    // When
    final var exception = new DataSourceNotFoundException(message, cause);

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
    final var exception = new DataSourceNotFoundException(cause);

    // Then
    assertNotNull(exception);
    assertEquals(cause, exception.getCause());
    assertNotNull(exception.getMessage());
  }

  /** Verifies that DataSourceNotFoundException extends DatabaseTesterException. */
  @Test
  @Tag("normal")
  @DisplayName("Extends DatabaseTesterException")
  void extendsDatabaseTesterException() {
    // Given
    final var exception = new DataSourceNotFoundException("Test");

    // When & Then
    assertInstanceOf(DatabaseTesterException.class, exception);
  }
}
