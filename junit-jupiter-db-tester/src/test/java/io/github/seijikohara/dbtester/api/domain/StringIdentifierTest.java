package io.github.seijikohara.dbtester.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link StringIdentifier} default methods.
 *
 * <p>This test class verifies the behavior of default methods provided by the {@link
 * StringIdentifier} interface using {@link ColumnName} as the concrete implementation.
 */
@DisplayName("StringIdentifier")
class StringIdentifierTest {

  /** Tests for the StringIdentifier interface default methods. */
  StringIdentifierTest() {}

  /** Tests for the compareTo(T) default method. */
  @Nested
  @DisplayName("compareTo(T) method")
  class CompareToMethod {

    /** Tests for the compareTo default method. */
    CompareToMethod() {}

    /** Verifies that compareTo returns zero when comparing equal values. */
    @Test
    @Tag("normal")
    @DisplayName("should return zero when comparing equal values")
    void shouldReturnZero_whenComparingEqualValues() {
      // Given
      final var identifier1 = new ColumnName("columnA");
      final var identifier2 = new ColumnName("columnA");

      // When
      final var result = identifier1.compareTo(identifier2);

      // Then
      assertEquals(0, result, "equal identifiers should return zero");
    }

    /** Verifies that compareTo returns negative when this is less than other. */
    @Test
    @Tag("normal")
    @DisplayName("should return negative when this is less than other")
    void shouldReturnNegative_whenThisIsLessThanOther() {
      // Given
      final var identifier1 = new ColumnName("columnA");
      final var identifier2 = new ColumnName("columnB");

      // When
      final var result = identifier1.compareTo(identifier2);

      // Then
      assertTrue(result < 0, "columnA should be less than columnB");
    }

    /** Verifies that compareTo returns positive when this is greater than other. */
    @Test
    @Tag("normal")
    @DisplayName("should return positive when this is greater than other")
    void shouldReturnPositive_whenThisIsGreaterThanOther() {
      // Given
      final var identifier1 = new ColumnName("columnB");
      final var identifier2 = new ColumnName("columnA");

      // When
      final var result = identifier1.compareTo(identifier2);

      // Then
      assertTrue(result > 0, "columnB should be greater than columnA");
    }

    /** Verifies that compareTo handles case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("should handle case-sensitive comparison")
    void shouldHandleCaseSensitiveComparison() {
      // Given
      final var lowercase = new ColumnName("columna");
      final var uppercase = new ColumnName("COLUMNA");

      // When
      final var result = lowercase.compareTo(uppercase);

      // Then
      assertTrue(result != 0, "case-sensitive comparison should distinguish different cases");
    }
  }

  /**
   * Tests for the validateNonBlankString(String, String) default method.
   *
   * <p>This method is tested indirectly through ColumnName constructor validation.
   */
  @Nested
  @DisplayName("validateNonBlankString(String, String) method")
  class ValidateNonBlankStringMethod {

    /** Tests for the validateNonBlankString default method. */
    ValidateNonBlankStringMethod() {}

    /** Verifies that validateNonBlankString accepts valid non-blank string. */
    @Test
    @Tag("normal")
    @DisplayName("should accept valid non-blank string")
    void shouldAcceptValidNonBlankString() {
      // When
      final var identifier = new ColumnName("validColumn");

      // Then
      assertEquals("validColumn", identifier.value());
    }

    /** Verifies that validateNonBlankString trims leading and trailing whitespace. */
    @Test
    @Tag("normal")
    @DisplayName("should trim leading and trailing whitespace")
    void shouldTrimLeadingAndTrailingWhitespace() {
      // When
      final var identifier = new ColumnName("  columnName  ");

      // Then
      assertEquals("columnName", identifier.value(), "value should be trimmed");
    }

    /** Verifies that validateNonBlankString throws exception when value is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenValueIsNull() {
      // Given
      final @Nullable String nullValue = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> new ColumnName(nullValue));

      assertEquals("Column name must not be null", exception.getMessage());
    }

    /** Verifies that validateNonBlankString throws exception when value is empty. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is empty")
    void shouldThrowException_whenValueIsEmpty() {
      // When & Then
      final var exception = assertThrows(IllegalArgumentException.class, () -> new ColumnName(""));

      assertEquals("Column name must not be blank", exception.getMessage());
    }

    /** Verifies that validateNonBlankString throws exception when value is blank. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is blank")
    void shouldThrowException_whenValueIsBlank() {
      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new ColumnName("   "));

      assertEquals("Column name must not be blank", exception.getMessage());
    }

    /** Verifies that validateNonBlankString throws exception when value contains only tabs. */
    @Test
    @Tag("edge-case")
    @DisplayName("should throw exception when value contains only tabs")
    void shouldThrowException_whenValueContainsOnlyTabs() {
      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new ColumnName("\t\t"));

      assertEquals("Column name must not be blank", exception.getMessage());
    }
  }
}
