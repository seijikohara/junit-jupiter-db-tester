package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSourceName}. */
@DisplayName("DataSourceName")
class DataSourceNameTest {

  /** Constructs test instance. */
  DataSourceNameTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid data source names are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid name")
    void createsInstance_withValidName() {
      // Given
      final var validName = "default";

      // When
      final var dataSourceName = new DataSourceName(validName);

      // Then
      assertNotNull(dataSourceName);
      assertEquals(validName, dataSourceName.value());
    }

    /** Verifies that data source names with hyphens are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with hyphenated name")
    void createsInstance_withHyphenatedName() {
      // Given
      final var hyphenatedName = "reporting-db";

      // When
      final var dataSourceName = new DataSourceName(hyphenatedName);

      // Then
      assertEquals(hyphenatedName, dataSourceName.value());
    }

    /** Verifies that data source names are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var nameWithSpaces = "  default  ";

      // When
      final var dataSourceName = new DataSourceName(nameWithSpaces);

      // Then
      assertEquals("default", dataSourceName.value());
    }

    /** Verifies that empty string is rejected. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when name is empty")
    void throwsException_whenNameIsEmpty() {
      // Given
      final var emptyName = "";

      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new DataSourceName(emptyName));
      assertEquals("Data source name must not be blank", exception.getMessage());
    }

    /** Verifies that blank string is rejected. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when name is blank")
    void throwsException_whenNameIsBlank() {
      // Given
      final var blankName = "   ";

      // When & Then
      final var exception =
          assertThrows(IllegalArgumentException.class, () -> new DataSourceName(blankName));
      assertEquals("Data source name must not be blank", exception.getMessage());
    }
  }

  /** Tests for compareTo method. */
  @Nested
  @DisplayName("compareTo() method")
  class CompareToMethod {

    /** Constructs test instance. */
    CompareToMethod() {}

    /** Verifies that data source names are ordered alphabetically. */
    @Test
    @Tag("normal")
    @DisplayName("Orders data source names alphabetically")
    void ordersAlphabetically() {
      // Given
      final var defaultDs = new DataSourceName("default");
      final var primaryDs = new DataSourceName("primary");
      final var secondaryDs = new DataSourceName("secondary");

      // When & Then
      assertTrue(defaultDs.compareTo(primaryDs) < 0);
      assertTrue(primaryDs.compareTo(secondaryDs) < 0);
      assertTrue(secondaryDs.compareTo(defaultDs) > 0);
    }

    /** Verifies that equal data source names have zero comparison result. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for equal names")
    void returnsZero_forEqualNames() {
      // Given
      final var dataSourceName1 = new DataSourceName("default");
      final var dataSourceName2 = new DataSourceName("default");

      // When
      final var result = dataSourceName1.compareTo(dataSourceName2);

      // Then
      assertEquals(0, result);
    }

    /** Verifies case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("Performs case-sensitive comparison")
    void performsCaseSensitiveComparison() {
      // Given
      final var lowercase = new DataSourceName("default");
      final var uppercase = new DataSourceName("DEFAULT");

      // When & Then
      assertTrue(uppercase.compareTo(lowercase) < 0);
      assertTrue(lowercase.compareTo(uppercase) > 0);
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
      final var dataSourceName1 = new DataSourceName("default");
      final var dataSourceName2 = new DataSourceName("default");

      // When & Then
      assertEquals(dataSourceName1, dataSourceName2);
      assertEquals(dataSourceName1.hashCode(), dataSourceName2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var defaultDs = new DataSourceName("default");
      final var primaryDs = new DataSourceName("primary");

      // When & Then
      assertNotEquals(defaultDs, primaryDs);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var dataSourceName1 = new DataSourceName("default");
      final var dataSourceName2 = new DataSourceName("  default  ");

      // When & Then
      assertEquals(dataSourceName1, dataSourceName2);
      assertEquals(dataSourceName1.hashCode(), dataSourceName2.hashCode());
    }
  }
}
