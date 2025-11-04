package io.github.seijikohara.dbtester.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ColumnName}. */
@DisplayName("ColumnName")
class ColumnNameTest {

  /** Tests for the ColumnName record. */
  ColumnNameTest() {}

  /** Tests for the ColumnName constructor. */
  @Nested
  @DisplayName("constructor")
  class ConstructorMethod {

    /** Tests for the constructor. */
    ConstructorMethod() {}

    /** Verifies that constructor accepts valid column name. */
    @Test
    @Tag("normal")
    @DisplayName("should accept valid column name")
    void shouldAcceptValidColumnName() {
      // When
      final var columnName = new ColumnName("columnA");

      // Then
      assertEquals("columnA", columnName.value());
    }

    /** Verifies that constructor trims leading and trailing whitespace. */
    @Test
    @Tag("normal")
    @DisplayName("should trim leading and trailing whitespace")
    void shouldTrimLeadingAndTrailingWhitespace() {
      // When
      final var columnName = new ColumnName("  columnB  ");

      // Then
      assertEquals("columnB", columnName.value(), "value should be trimmed");
    }

    /** Verifies that constructor throws exception when value is null. */
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

    /** Verifies that constructor throws exception when value is empty. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is empty")
    void shouldThrowException_whenValueIsEmpty() {
      // When & Then
      final var exception = assertThrows(IllegalArgumentException.class, () -> new ColumnName(""));

      assertEquals("Column name must not be blank", exception.getMessage());
    }

    /** Verifies that constructor throws exception when value is blank. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is blank")
    void shouldThrowException_whenValueIsBlank() {
      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new ColumnName("   "));

      assertEquals("Column name must not be blank", exception.getMessage());
    }

    /** Verifies that constructor throws exception when value contains only tabs. */
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
