package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SchemaName}. */
@DisplayName("SchemaName")
class SchemaNameTest {

  /** Constructs test instance. */
  SchemaNameTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid schema names are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid name")
    void createsInstance_withValidName() {
      // Given
      final var validName = "public";

      // When
      final var schemaName = new SchemaName(validName);

      // Then
      assertNotNull(schemaName);
      assertEquals(validName, schemaName.value());
    }

    /** Verifies that schema names are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var nameWithSpaces = "  public  ";

      // When
      final var schemaName = new SchemaName(nameWithSpaces);

      // Then
      assertEquals("public", schemaName.value());
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
          assertThrows(IllegalArgumentException.class, () -> new SchemaName(emptyName));
      assertEquals("Schema name must not be blank", exception.getMessage());
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
          assertThrows(IllegalArgumentException.class, () -> new SchemaName(blankName));
      assertEquals("Schema name must not be blank", exception.getMessage());
    }
  }

  /** Tests for compareTo method. */
  @Nested
  @DisplayName("compareTo() method")
  class CompareToMethod {

    /** Constructs test instance. */
    CompareToMethod() {}

    /** Verifies that schema names are ordered alphabetically. */
    @Test
    @Tag("normal")
    @DisplayName("Orders schema names alphabetically")
    void ordersAlphabetically() {
      // Given
      final var publicSchema = new SchemaName("public");
      final var privateSchema = new SchemaName("private");
      final var adminSchema = new SchemaName("admin");

      // When & Then
      assertTrue(adminSchema.compareTo(privateSchema) < 0);
      assertTrue(privateSchema.compareTo(publicSchema) < 0);
      assertTrue(publicSchema.compareTo(adminSchema) > 0);
    }

    /** Verifies that equal schema names have zero comparison result. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for equal names")
    void returnsZero_forEqualNames() {
      // Given
      final var schemaName1 = new SchemaName("public");
      final var schemaName2 = new SchemaName("public");

      // When
      final var result = schemaName1.compareTo(schemaName2);

      // Then
      assertEquals(0, result);
    }

    /** Verifies case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("Performs case-sensitive comparison")
    void performsCaseSensitiveComparison() {
      // Given
      final var lowercase = new SchemaName("public");
      final var uppercase = new SchemaName("PUBLIC");

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
      final var schemaName1 = new SchemaName("public");
      final var schemaName2 = new SchemaName("public");

      // When & Then
      assertEquals(schemaName1, schemaName2);
      assertEquals(schemaName1.hashCode(), schemaName2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var publicSchema = new SchemaName("public");
      final var privateSchema = new SchemaName("private");

      // When & Then
      assertNotEquals(publicSchema, privateSchema);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var schemaName1 = new SchemaName("public");
      final var schemaName2 = new SchemaName("  public  ");

      // When & Then
      assertEquals(schemaName1, schemaName2);
      assertEquals(schemaName1.hashCode(), schemaName2.hashCode());
    }
  }
}
