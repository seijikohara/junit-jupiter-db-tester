package io.github.seijikohara.dbtester.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DatabaseTesterException}. */
@DisplayName("DatabaseTesterException")
class DatabaseTesterExceptionTest {

  /** Tests for the DatabaseTesterException class. */
  DatabaseTesterExceptionTest() {}

  /** Tests for the single-argument constructor DatabaseTesterException(String). */
  @Nested
  @DisplayName("DatabaseTesterException(String) constructor")
  class SingleArgumentConstructor {

    /** Tests for the single-argument constructor. */
    SingleArgumentConstructor() {}

    /** Verifies that constructor creates exception with message. */
    @Test
    @Tag("normal")
    @DisplayName("should create exception with message")
    void shouldCreateException_withMessage() {
      // Given
      final var message = "test error message";

      // When
      final var exception = new DatabaseTesterException(message);

      // Then
      assertEquals(message, exception.getMessage());
    }

    /** Verifies that constructor throws exception when message is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when message is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenMessageIsNull() {
      // Given
      final @Nullable String nullMessage = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> new DatabaseTesterException(nullMessage));

      assertEquals("message must not be null", exception.getMessage());
    }
  }

  /** Tests for the two-argument constructor DatabaseTesterException(String, Throwable). */
  @Nested
  @DisplayName("DatabaseTesterException(String, Throwable) constructor")
  class TwoArgumentConstructor {

    /** Tests for the two-argument constructor. */
    TwoArgumentConstructor() {}

    /** Verifies that constructor creates exception with message and cause. */
    @Test
    @Tag("normal")
    @DisplayName("should create exception with message and cause")
    void shouldCreateException_withMessageAndCause() {
      // Given
      final var message = "test error message";
      final var cause = new RuntimeException("root cause");

      // When
      final var exception = new DatabaseTesterException(message, cause);

      // Then
      assertEquals(message, exception.getMessage());
      assertSame(cause, exception.getCause());
    }

    /** Verifies that constructor throws exception when message is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when message is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenMessageIsNull() {
      // Given
      final @Nullable String nullMessage = null;
      final var cause = new RuntimeException("root cause");

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> new DatabaseTesterException(nullMessage, cause));

      assertEquals("message must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when cause is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when cause is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenCauseIsNull() {
      // Given
      final var message = "test error message";
      final @Nullable Throwable nullCause = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> new DatabaseTesterException(message, nullCause));

      assertEquals("cause must not be null", exception.getMessage());
    }
  }

  /** Tests for the single-cause constructor DatabaseTesterException(Throwable). */
  @Nested
  @DisplayName("DatabaseTesterException(Throwable) constructor")
  class SingleCauseConstructor {

    /** Tests for the single-cause constructor. */
    SingleCauseConstructor() {}

    /** Verifies that constructor creates exception with cause. */
    @Test
    @Tag("normal")
    @DisplayName("should create exception with cause")
    void shouldCreateException_withCause() {
      // Given
      final var cause = new RuntimeException("root cause");

      // When
      final var exception = new DatabaseTesterException(cause);

      // Then
      assertSame(cause, exception.getCause());
    }

    /** Verifies that constructor throws exception when cause is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when cause is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenCauseIsNull() {
      // Given
      final @Nullable Throwable nullCause = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> new DatabaseTesterException(nullCause));

      assertEquals("cause must not be null", exception.getMessage());
    }
  }
}
