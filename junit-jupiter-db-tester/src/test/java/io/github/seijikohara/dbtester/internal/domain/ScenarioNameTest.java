package io.github.seijikohara.dbtester.internal.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ScenarioName}. */
@DisplayName("ScenarioName")
class ScenarioNameTest {

  /** Constructs test instance. */
  ScenarioNameTest() {}

  /** Tests for constructor validation. */
  @Nested
  @DisplayName("Constructor validation")
  class ConstructorValidation {

    /** Constructs test instance. */
    ConstructorValidation() {}

    /** Verifies that valid scenario names are accepted. */
    @Test
    @Tag("normal")
    @DisplayName("Creates instance with valid name")
    void createsInstance_withValidName() {
      // Given
      final var validName = "testCreateUser";

      // When
      final var scenarioName = new ScenarioName(validName);

      // Then
      assertNotNull(scenarioName);
      assertEquals(validName, scenarioName.value());
    }

    /** Verifies that scenario names are trimmed. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trims leading and trailing whitespace")
    void trimsWhitespace() {
      // Given
      final var nameWithSpaces = "  testCreateUser  ";

      // When
      final var scenarioName = new ScenarioName(nameWithSpaces);

      // Then
      assertEquals("testCreateUser", scenarioName.value());
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
          assertThrows(IllegalArgumentException.class, () -> new ScenarioName(emptyName));
      assertEquals("Scenario name must not be blank", exception.getMessage());
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
          assertThrows(IllegalArgumentException.class, () -> new ScenarioName(blankName));
      assertEquals("Scenario name must not be blank", exception.getMessage());
    }
  }

  /** Tests for compareTo method. */
  @Nested
  @DisplayName("compareTo() method")
  class CompareToMethod {

    /** Constructs test instance. */
    CompareToMethod() {}

    /** Verifies that scenario names are ordered alphabetically. */
    @Test
    @Tag("normal")
    @DisplayName("Orders scenario names alphabetically")
    void ordersAlphabetically() {
      // Given
      final var testCreateUser = new ScenarioName("testCreateUser");
      final var testUpdateUser = new ScenarioName("testUpdateUser");
      final var testDeleteUser = new ScenarioName("testDeleteUser");

      // When & Then
      assertTrue(testCreateUser.compareTo(testDeleteUser) < 0);
      assertTrue(testDeleteUser.compareTo(testUpdateUser) < 0);
      assertTrue(testUpdateUser.compareTo(testCreateUser) > 0);
    }

    /** Verifies that equal scenario names have zero comparison result. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for equal names")
    void returnsZero_forEqualNames() {
      // Given
      final var scenarioName1 = new ScenarioName("testCreateUser");
      final var scenarioName2 = new ScenarioName("testCreateUser");

      // When
      final var result = scenarioName1.compareTo(scenarioName2);

      // Then
      assertEquals(0, result);
    }

    /** Verifies case-sensitive comparison. */
    @Test
    @Tag("edge-case")
    @DisplayName("Performs case-sensitive comparison")
    void performsCaseSensitiveComparison() {
      // Given
      final var lowercase = new ScenarioName("testcreateuser");
      final var camelCase = new ScenarioName("testCreateUser");

      // When & Then
      assertTrue(camelCase.compareTo(lowercase) < 0);
      assertTrue(lowercase.compareTo(camelCase) > 0);
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
      final var scenarioName1 = new ScenarioName("testCreateUser");
      final var scenarioName2 = new ScenarioName("testCreateUser");

      // When & Then
      assertEquals(scenarioName1, scenarioName2);
      assertEquals(scenarioName1.hashCode(), scenarioName2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var createUser = new ScenarioName("testCreateUser");
      final var updateUser = new ScenarioName("testUpdateUser");

      // When & Then
      assertNotEquals(createUser, updateUser);
    }

    /** Verifies that trimmed values are considered equal. */
    @Test
    @Tag("edge-case")
    @DisplayName("Trimmed values are equal to non-trimmed equivalent")
    void trimmedValues_areEqualToNonTrimmedEquivalent() {
      // Given
      final var scenarioName1 = new ScenarioName("testCreateUser");
      final var scenarioName2 = new ScenarioName("  testCreateUser  ");

      // When & Then
      assertEquals(scenarioName1, scenarioName2);
      assertEquals(scenarioName1.hashCode(), scenarioName2.hashCode());
    }
  }
}
