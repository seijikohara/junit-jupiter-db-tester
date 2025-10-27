package io.github.seijikohara.dbtester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DatabaseTesterException}. */
@DisplayName("DatabaseTesterException")
class DatabaseTesterExceptionTest {

  /** Constructs test instance. */
  DatabaseTesterExceptionTest() {}

  /** Tests for constructor with message parameter. */
  @Nested
  @DisplayName("Constructor with message")
  class ConstructorWithMessage {

    /** Constructs test instance. */
    ConstructorWithMessage() {}

    /** Verifies that exception is created with message. */
    @Test
    @Tag("normal")
    @DisplayName("Creates exception with message")
    void createsException_withMessage() {
      // Given
      final var message = "Test error message";

      // When
      final var exception = new DatabaseTesterException(message);

      // Then
      assertNotNull(exception);
      assertEquals(message, exception.getMessage());
      assertNull(exception.getCause());
    }
  }

  /** Tests for constructor with message and cause parameters. */
  @Nested
  @DisplayName("Constructor with message and cause")
  class ConstructorWithMessageAndCause {

    /** Constructs test instance. */
    ConstructorWithMessageAndCause() {}

    /** Verifies that exception is created with message and cause. */
    @Test
    @Tag("normal")
    @DisplayName("Creates exception with message and cause")
    void createsException_withMessageAndCause() {
      // Given
      final var message = "Test error message";
      final var cause = new RuntimeException("Underlying cause");

      // When
      final var exception = new DatabaseTesterException(message, cause);

      // Then
      assertNotNull(exception);
      assertEquals(message, exception.getMessage());
      assertEquals(cause, exception.getCause());
    }
  }

  /** Tests for constructor with cause parameter. */
  @Nested
  @DisplayName("Constructor with cause")
  class ConstructorWithCause {

    /** Constructs test instance. */
    ConstructorWithCause() {}

    /** Verifies that exception is created with cause. */
    @Test
    @Tag("normal")
    @DisplayName("Creates exception with cause")
    void createsException_withCause() {
      // Given
      final var cause = new RuntimeException("Underlying cause");

      // When
      final var exception = new DatabaseTesterException(cause);

      // Then
      assertNotNull(exception);
      assertEquals(cause, exception.getCause());
      assertNotNull(exception.getMessage());
    }
  }

  /** Tests for exception inheritance. */
  @Nested
  @DisplayName("Exception inheritance")
  class ExceptionInheritance {

    /** Constructs test instance. */
    ExceptionInheritance() {}

    /** Verifies that DatabaseTesterException extends RuntimeException. */
    @Test
    @Tag("normal")
    @DisplayName("Extends RuntimeException")
    void extendsRuntimeException() {
      // Given
      final var exception = new DatabaseTesterException("Test");

      // When & Then
      assertInstanceOf(RuntimeException.class, exception);
    }
  }
}
