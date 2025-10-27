package io.github.seijikohara.dbtester.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.internal.loader.DataSetLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Configuration}. */
@DisplayName("Configuration")
class ConfigurationTest {

  /** Constructs test instance. */
  ConfigurationTest() {}

  /** Tests for defaults factory method. */
  @Nested
  @DisplayName("defaults() method")
  class DefaultsMethod {

    /** Constructs test instance. */
    DefaultsMethod() {}

    /** Verifies that defaults are created with standard values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates configuration with default values")
    void createsConfiguration_withDefaultValues() {
      // When
      final var config = Configuration.defaults();

      // Then
      assertNotNull(config);
      assertNotNull(config.conventions());
      assertEquals(ConventionSettings.standard(), config.conventions());
      assertNotNull(config.operations());
      assertEquals(OperationDefaults.standard(), config.operations());
      assertNotNull(config.loader());
    }
  }

  /** Tests for withConventions factory method. */
  @Nested
  @DisplayName("withConventions() method")
  class WithConventionsMethod {

    /** Constructs test instance. */
    WithConventionsMethod() {}

    /** Verifies that custom conventions are used. */
    @Test
    @Tag("normal")
    @DisplayName("Creates configuration with custom conventions")
    void createsConfiguration_withCustomConventions() {
      // Given
      final var customConventions = new ConventionSettings("test-data", "/expected", "[Test]");

      // When
      final var config = Configuration.withConventions(customConventions);

      // Then
      assertNotNull(config);
      assertEquals(customConventions, config.conventions());
      assertEquals(OperationDefaults.standard(), config.operations());
      assertNotNull(config.loader());
    }
  }

  /** Tests for withOperations factory method. */
  @Nested
  @DisplayName("withOperations() method")
  class WithOperationsMethod {

    /** Constructs test instance. */
    WithOperationsMethod() {}

    /** Verifies that custom operations are used. */
    @Test
    @Tag("normal")
    @DisplayName("Creates configuration with custom operations")
    void createsConfiguration_withCustomOperations() {
      // Given
      final var customOperations =
          new OperationDefaults(
              io.github.seijikohara.dbtester.api.operation.Operation.INSERT,
              io.github.seijikohara.dbtester.api.operation.Operation.UPDATE);

      // When
      final var config = Configuration.withOperations(customOperations);

      // Then
      assertNotNull(config);
      assertEquals(ConventionSettings.standard(), config.conventions());
      assertEquals(customOperations, config.operations());
      assertNotNull(config.loader());
    }
  }

  /** Tests for withLoader factory method. */
  @Nested
  @DisplayName("withLoader() method")
  class WithLoaderMethod {

    /** Constructs test instance. */
    WithLoaderMethod() {}

    /** Verifies that custom loader is used. */
    @Test
    @Tag("normal")
    @DisplayName("Creates configuration with custom loader")
    void createsConfiguration_withCustomLoader() {
      // Given
      final var customLoader = mock(DataSetLoader.class);

      // When
      final var config = Configuration.withLoader(customLoader);

      // Then
      assertNotNull(config);
      assertEquals(ConventionSettings.standard(), config.conventions());
      assertEquals(OperationDefaults.standard(), config.operations());
      assertEquals(customLoader, config.loader());
    }
  }

  /** Tests for constructor with custom values. */
  @Nested
  @DisplayName("Constructor with custom values")
  class ConstructorWithCustomValues {

    /** Constructs test instance. */
    ConstructorWithCustomValues() {}

    /** Verifies that configuration is created with all custom values. */
    @Test
    @Tag("normal")
    @DisplayName("Creates configuration with all custom values")
    void createsConfiguration_withAllCustomValues() {
      // Given
      final var customConventions = new ConventionSettings("test-data", "/expected", "[Test]");
      final var customOperations =
          new OperationDefaults(
              io.github.seijikohara.dbtester.api.operation.Operation.INSERT,
              io.github.seijikohara.dbtester.api.operation.Operation.UPDATE);
      final var customLoader = mock(DataSetLoader.class);

      // When
      final var config = new Configuration(customConventions, customOperations, customLoader);

      // Then
      assertNotNull(config);
      assertEquals(customConventions, config.conventions());
      assertEquals(customOperations, config.operations());
      assertEquals(customLoader, config.loader());
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
      final var conventions = ConventionSettings.standard();
      final var operations = OperationDefaults.standard();
      final var loader = mock(DataSetLoader.class);
      final var config1 = new Configuration(conventions, operations, loader);
      final var config2 = new Configuration(conventions, operations, loader);

      // When & Then
      assertEquals(config1, config2);
      assertEquals(config1.hashCode(), config2.hashCode());
    }

    /** Verifies that different values produce different instances. */
    @Test
    @Tag("normal")
    @DisplayName("Different values produce different instances")
    void differentValues_produceDifferentInstances() {
      // Given
      final var config1 = Configuration.defaults();
      final var customConventions = new ConventionSettings("test-data", "/expected", "[Test]");
      final var config2 = Configuration.withConventions(customConventions);

      // When & Then
      assertNotEquals(config1, config2);
    }
  }
}
