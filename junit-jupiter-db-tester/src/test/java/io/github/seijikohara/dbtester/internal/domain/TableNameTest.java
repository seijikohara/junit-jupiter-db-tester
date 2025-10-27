package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link TableName}. */
@DisplayName("TableName")
class TableNameTest {

  /** Constructs test instance. */
  TableNameTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid table names are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid name")
    void createsInstance_withValidName() {
      // Given
      final var validName = "USERS";

      // When
      final var tableName = new TableName(validName);

      // Then
      assertNotNull(tableName);
      assertEquals(validName, tableName.value());
    }

    /** Verifies that table names are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var nameWithSpaces = "  USERS  ";

      // When
      final var tableName = new TableName(nameWithSpaces);

      // Then
      assertEquals("USERS", tableName.value());
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
          assertThrows(IllegalArgumentException.class, () -> new TableName(emptyName));
      assertEquals("Table name must not be blank", exception.getMessage());
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
          assertThrows(IllegalArgumentException.class, () -> new TableName(blankName));
      assertEquals("Table name must not be blank", exception.getMessage());
    }
  }

  /** Tests for compareTo method. */
  @Nested
  @DisplayName("compareTo() method")
  class CompareToMethod {

    /** Constructs test instance. */
    CompareToMethod() {}

    /** Verifies that table names are ordered alphabetically. */
    @Test
    @Tag("normal")
    @DisplayName("Orders table names alphabetically")
    void ordersAlphabetically() {
      // Given
      final var users = new TableName("USERS");
      final var products = new TableName("PRODUCTS");
      final var orders = new TableName("ORDERS");

      // When & Then
      assertTrue(orders.compareTo(products) < 0);
      assertTrue(products.compareTo(users) < 0);
      assertTrue(users.compareTo(orders) > 0);
    }

    /** Verifies that equal table names have zero comparison result. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for equal names")
    void returnsZero_forEqualNames() {
      // Given
      final var tableName1 = new TableName("USERS");
      final var tableName2 = new TableName("USERS");

      // When
      final var result = tableName1.compareTo(tableName2);

      // Then
      assertEquals(0, result);
    }

    /** Verifies case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("Performs case-sensitive comparison")
    void performsCaseSensitiveComparison() {
      // Given
      final var lowercase = new TableName("users");
      final var uppercase = new TableName("USERS");

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
      final var tableName1 = new TableName("USERS");
      final var tableName2 = new TableName("USERS");

      // When & Then
      assertEquals(tableName1, tableName2);
      assertEquals(tableName1.hashCode(), tableName2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var users = new TableName("USERS");
      final var products = new TableName("PRODUCTS");

      // When & Then
      assertNotEquals(users, products);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var tableName1 = new TableName("USERS");
      final var tableName2 = new TableName("  USERS  ");

      // When & Then
      assertEquals(tableName1, tableName2);
      assertEquals(tableName1.hashCode(), tableName2.hashCode());
    }
  }
}
