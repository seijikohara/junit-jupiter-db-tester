package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ColumnName}. */
@DisplayName("ColumnName")
class ColumnNameTest {

  /** Constructs test instance. */
  ColumnNameTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid column names are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid name")
    void createsInstance_withValidName() {
      // Given
      final var validName = "USER_ID";

      // When
      final var columnName = new ColumnName(validName);

      // Then
      assertNotNull(columnName);
      assertEquals(validName, columnName.value());
    }

    /** Verifies that column names are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var nameWithSpaces = "  USER_ID  ";

      // When
      final var columnName = new ColumnName(nameWithSpaces);

      // Then
      assertEquals("USER_ID", columnName.value());
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
          assertThrows(IllegalArgumentException.class, () -> new ColumnName(emptyName));
      assertEquals("Column name must not be blank", exception.getMessage());
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
          assertThrows(IllegalArgumentException.class, () -> new ColumnName(blankName));
      assertEquals("Column name must not be blank", exception.getMessage());
    }
  }

  /** Tests for compareTo method. */
  @Nested
  @DisplayName("compareTo() method")
  class CompareToMethod {

    /** Constructs test instance. */
    CompareToMethod() {}

    /** Verifies that column names are ordered alphabetically. */
    @Test
    @Tag("normal")
    @DisplayName("Orders column names alphabetically")
    void ordersAlphabetically() {
      // Given
      final var userId = new ColumnName("USER_ID");
      final var userName = new ColumnName("USER_NAME");
      final var email = new ColumnName("EMAIL");

      // When & Then
      assertTrue(email.compareTo(userId) < 0);
      assertTrue(userId.compareTo(userName) < 0);
      assertTrue(userName.compareTo(email) > 0);
    }

    /** Verifies that equal column names have zero comparison result. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for equal names")
    void returnsZero_forEqualNames() {
      // Given
      final var columnName1 = new ColumnName("USER_ID");
      final var columnName2 = new ColumnName("USER_ID");

      // When
      final var result = columnName1.compareTo(columnName2);

      // Then
      assertEquals(0, result);
    }

    /** Verifies case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("Performs case-sensitive comparison")
    void performsCaseSensitiveComparison() {
      // Given
      final var lowercase = new ColumnName("user_id");
      final var uppercase = new ColumnName("USER_ID");

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
      final var columnName1 = new ColumnName("USER_ID");
      final var columnName2 = new ColumnName("USER_ID");

      // When & Then
      assertEquals(columnName1, columnName2);
      assertEquals(columnName1.hashCode(), columnName2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var userId = new ColumnName("USER_ID");
      final var userName = new ColumnName("USER_NAME");

      // When & Then
      assertNotEquals(userId, userName);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var columnName1 = new ColumnName("USER_ID");
      final var columnName2 = new ColumnName("  USER_ID  ");

      // When & Then
      assertEquals(columnName1, columnName2);
      assertEquals(columnName1.hashCode(), columnName2.hashCode());
    }
  }
}
