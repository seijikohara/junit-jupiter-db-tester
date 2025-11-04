package io.github.seijikohara.dbtester.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ValidationException}. */
@DisplayName("ValidationException")
class ValidationExceptionTest {

  /** Tests for the ValidationException class. */
  ValidationExceptionTest() {}

  /** Tests for the single-argument constructor ValidationException(String). */
  @Nested
  @DisplayName("ValidationException(String) constructor")
  class SingleArgumentConstructor {

    /** Tests for the single-argument constructor. */
    SingleArgumentConstructor() {}

    /** Verifies that constructor creates exception with message. */
    @Test
    @Tag("normal")
    @DisplayName("should create exception with message")
    void shouldCreateException_withMessage() {
      // Given
      final var message = "validation failed";

      // When
      final var exception = new ValidationException(message);

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
          assertThrows(NullPointerException.class, () -> new ValidationException(nullMessage));

      assertEquals("message must not be null", exception.getMessage());
    }
  }

  /** Tests for the two-argument constructor ValidationException(String, Throwable). */
  @Nested
  @DisplayName("ValidationException(String, Throwable) constructor")
  class TwoArgumentConstructor {

    /** Tests for the two-argument constructor. */
    TwoArgumentConstructor() {}

    /** Verifies that constructor creates exception with message and cause. */
    @Test
    @Tag("normal")
    @DisplayName("should create exception with message and cause")
    void shouldCreateException_withMessageAndCause() {
      // Given
      final var message = "validation failed";
      final var cause = new RuntimeException("root cause");

      // When
      final var exception = new ValidationException(message, cause);

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
              NullPointerException.class, () -> new ValidationException(nullMessage, cause));

      assertEquals("message must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when cause is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when cause is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenCauseIsNull() {
      // Given
      final var message = "validation failed";
      final @Nullable Throwable nullCause = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> new ValidationException(message, nullCause));

      assertEquals("cause must not be null", exception.getMessage());
    }
  }

  /** Tests for the single-cause constructor ValidationException(Throwable). */
  @Nested
  @DisplayName("ValidationException(Throwable) constructor")
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
      final var exception = new ValidationException(cause);

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
          assertThrows(NullPointerException.class, () -> new ValidationException(nullCause));

      assertEquals("cause must not be null", exception.getMessage());
    }
  }
}
