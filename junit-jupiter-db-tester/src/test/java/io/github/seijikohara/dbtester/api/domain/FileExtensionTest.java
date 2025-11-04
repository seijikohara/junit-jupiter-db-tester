package io.github.seijikohara.dbtester.api.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link FileExtension}. */
@DisplayName("FileExtension")
class FileExtensionTest {

  /** Tests for the FileExtension record. */
  FileExtensionTest() {}

  /** Tests for the FileExtension constructor. */
  @Nested
  @DisplayName("constructor")
  class ConstructorMethod {

    /** Tests for the constructor. */
    ConstructorMethod() {}

    /** Verifies that constructor normalizes extension with dot to lowercase. */
    @Test
    @Tag("normal")
    @DisplayName("should normalize extension with dot to lowercase")
    void shouldNormalizeExtension_withDot_toLowerCase() {
      // When
      final var extension = new FileExtension(".CSV");

      // Then
      assertEquals(".csv", extension.value(), "extension should be normalized to lowercase");
    }

    /** Verifies that constructor normalizes extension without dot to lowercase with dot. */
    @Test
    @Tag("normal")
    @DisplayName("should normalize extension without dot to lowercase with dot")
    void shouldNormalizeExtension_withoutDot_toLowercaseWithDot() {
      // When
      final var extension = new FileExtension("CSV");

      // Then
      assertEquals(".csv", extension.value(), "extension should have leading dot and be lowercase");
    }

    /** Verifies that constructor accepts lowercase extension with dot. */
    @Test
    @Tag("normal")
    @DisplayName("should accept lowercase extension with dot")
    void shouldAccept_lowercaseExtension_withDot() {
      // When
      final var extension = new FileExtension(".csv");

      // Then
      assertEquals(".csv", extension.value(), "extension should remain unchanged");
    }

    /** Verifies that constructor accepts lowercase extension without dot. */
    @Test
    @Tag("normal")
    @DisplayName("should accept lowercase extension without dot")
    void shouldAccept_lowercaseExtension_withoutDot() {
      // When
      final var extension = new FileExtension("csv");

      // Then
      assertEquals(".csv", extension.value(), "extension should have leading dot added");
    }

    /** Verifies that constructor normalizes mixed case extension. */
    @Test
    @Tag("normal")
    @DisplayName("should normalize mixed case extension")
    void shouldNormalize_mixedCaseExtension() {
      // When
      final var extension = new FileExtension(".Csv");

      // Then
      assertEquals(".csv", extension.value(), "extension should be normalized to lowercase");
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
          assertThrows(NullPointerException.class, () -> new FileExtension(nullValue));

      assertEquals("File extension must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when value is dot only. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is dot only")
    void shouldThrowException_whenValueIsDotOnly() {
      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new FileExtension("."));

      assertEquals("File extension must not be empty after '.'", exception.getMessage());
    }

    /** Verifies that constructor throws exception when value is empty string. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when value is empty string")
    void shouldThrowException_whenValueIsEmptyString() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> new FileExtension(""));
    }
  }

  /** Tests for the fromFileName(String) static method. */
  @Nested
  @DisplayName("fromFileName(String) method")
  class FromFileNameMethod {

    /** Tests for the fromFileName method. */
    FromFileNameMethod() {}

    /** Verifies that fromFileName extracts extension from simple file name. */
    @Test
    @Tag("normal")
    @DisplayName("should extract extension from simple file name")
    void shouldExtractExtension_fromSimpleFileName() {
      // When
      final var result = FileExtension.fromFileName("table.csv");

      // Then
      assertAll(
          "extracted extension",
          () -> assertTrue(result.isPresent(), "extension should be present"),
          () -> assertEquals(".csv", result.orElseThrow().value(), "extension should be .csv"));
    }

    /** Verifies that fromFileName extracts and normalizes uppercase extension. */
    @Test
    @Tag("normal")
    @DisplayName("should extract and normalize uppercase extension")
    void shouldExtractAndNormalize_uppercaseExtension() {
      // When
      final var result = FileExtension.fromFileName("table.CSV");

      // Then
      assertAll(
          "extracted and normalized extension",
          () -> assertTrue(result.isPresent(), "extension should be present"),
          () ->
              assertEquals(
                  ".csv", result.orElseThrow().value(), "extension should be normalized to .csv"));
    }

    /** Verifies that fromFileName extracts extension from file name with multiple dots. */
    @Test
    @Tag("normal")
    @DisplayName("should extract extension from file name with multiple dots")
    void shouldExtractExtension_fromFileNameWithMultipleDots() {
      // When
      final var result = FileExtension.fromFileName("data.backup.csv");

      // Then
      assertAll(
          "extracted extension",
          () -> assertTrue(result.isPresent(), "extension should be present"),
          () -> assertEquals(".csv", result.orElseThrow().value(), "extension should be .csv"));
    }

    /** Verifies that fromFileName returns empty when file name has no extension. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return empty when file name has no extension")
    void shouldReturnEmpty_whenFileNameHasNoExtension() {
      // When
      final var result = FileExtension.fromFileName("table");

      // Then
      assertEquals(Optional.empty(), result, "result should be empty");
    }

    /** Verifies that fromFileName returns empty when file name ends with dot. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return empty when file name ends with dot")
    void shouldReturnEmpty_whenFileNameEndsWithDot() {
      // When
      final var result = FileExtension.fromFileName("table.");

      // Then
      assertEquals(Optional.empty(), result, "result should be empty");
    }

    /** Verifies that fromFileName returns empty when file name starts with dot. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return empty when file name starts with dot")
    void shouldReturnEmpty_whenFileNameStartsWithDot() {
      // When
      final var result = FileExtension.fromFileName(".gitignore");

      // Then
      assertEquals(Optional.empty(), result, "result should be empty for dot files");
    }

    /** Verifies that fromFileName extracts extension from path with directories. */
    @Test
    @Tag("normal")
    @DisplayName("should extract extension from path with directories")
    void shouldExtractExtension_fromPathWithDirectories() {
      // When
      final var result = FileExtension.fromFileName("path/to/file.txt");

      // Then
      assertAll(
          "extracted extension from path",
          () -> assertTrue(result.isPresent(), "extension should be present"),
          () -> assertEquals(".txt", result.orElseThrow().value(), "extension should be .txt"));
    }
  }

  /** Tests for the matches(String) method. */
  @Nested
  @DisplayName("matches(String) method")
  class MatchesMethod {

    /** Tests for the matches method. */
    MatchesMethod() {}

    /** Verifies that matches returns true when file name has matching extension. */
    @Test
    @Tag("normal")
    @DisplayName("should return true when file name has matching extension")
    void shouldReturnTrue_whenFileNameHasMatchingExtension() {
      // Given
      final var extension = new FileExtension(".csv");

      // When
      final var result = extension.matches("table.csv");

      // Then
      assertTrue(result, "should match table.csv");
    }

    /** Verifies that matches is case-insensitive. */
    @Test
    @Tag("normal")
    @DisplayName("should be case-insensitive")
    void shouldBeCaseInsensitive() {
      // Given
      final var extension = new FileExtension(".csv");

      // When & Then
      assertAll(
          "case-insensitive matching",
          () -> assertTrue(extension.matches("table.CSV"), "should match table.CSV"),
          () -> assertTrue(extension.matches("table.Csv"), "should match table.Csv"),
          () -> assertTrue(extension.matches("table.csv"), "should match table.csv"));
    }

    /** Verifies that matches returns false when extension does not match. */
    @Test
    @Tag("normal")
    @DisplayName("should return false when extension does not match")
    void shouldReturnFalse_whenExtensionDoesNotMatch() {
      // Given
      final var extension = new FileExtension(".csv");

      // When
      final var result = extension.matches("table.txt");

      // Then
      assertFalse(result, "should not match table.txt");
    }

    /** Verifies that matches returns false when file name has no extension. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return false when file name has no extension")
    void shouldReturnFalse_whenFileNameHasNoExtension() {
      // Given
      final var extension = new FileExtension(".csv");

      // When
      final var result = extension.matches("table");

      // Then
      assertFalse(result, "should not match file without extension");
    }

    /** Verifies that matches works with file paths. */
    @Test
    @Tag("normal")
    @DisplayName("should work with file paths")
    void shouldWorkWithFilePaths() {
      // Given
      final var extension = new FileExtension(".txt");

      // When
      final var result = extension.matches("path/to/file.txt");

      // Then
      assertTrue(result, "should match path/to/file.txt");
    }
  }

  /** Tests for the value() accessor method. */
  @Nested
  @DisplayName("value() method")
  class ValueMethod {

    /** Tests for the value accessor method. */
    ValueMethod() {}

    /** Verifies that value returns normalized extension. */
    @Test
    @Tag("normal")
    @DisplayName("should return normalized extension")
    void shouldReturnNormalizedExtension() {
      // Given
      final var extension = new FileExtension("CSV");

      // When
      final var result = extension.value();

      // Then
      assertEquals(".csv", result, "value should return normalized extension");
    }
  }
}
