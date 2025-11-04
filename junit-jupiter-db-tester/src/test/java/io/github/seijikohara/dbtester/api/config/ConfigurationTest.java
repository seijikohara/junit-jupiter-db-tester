package io.github.seijikohara.dbtester.api.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.github.seijikohara.dbtester.api.loader.DataSetLoader;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link Configuration}. */
@ExtendWith(MockitoExtension.class)
@DisplayName("Configuration")
class ConfigurationTest {

  /** Tests for the Configuration record. */
  ConfigurationTest() {}

  /** Mock loader for testing. */
  private DataSetLoader mockLoader;

  /** Standard conventions instance for testing. */
  private ConventionSettings conventions;

  /** Standard operations instance for testing. */
  private OperationDefaults operations;

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    conventions = ConventionSettings.standard();
    operations = OperationDefaults.standard();
    mockLoader = mock(DataSetLoader.class);
  }

  /** Tests for the Configuration constructor. */
  @Nested
  @DisplayName("constructor")
  class ConstructorMethod {

    /** Tests for the constructor. */
    ConstructorMethod() {}

    /** Verifies that constructor creates instance with valid parameters. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance when all parameters are valid")
    void shouldCreateInstance_whenAllParametersAreValid() {
      // When
      final var configuration = new Configuration(conventions, operations, mockLoader);

      // Then
      assertAll(
          "configuration components",
          () -> assertEquals(conventions, configuration.conventions()),
          () -> assertEquals(operations, configuration.operations()),
          () -> assertEquals(mockLoader, configuration.loader()));
    }

    /** Verifies that constructor throws exception when conventions is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when conventions is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenConventionsIsNull() {
      // Given
      final @Nullable ConventionSettings nullConventions = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new Configuration(nullConventions, operations, mockLoader));

      assertEquals("conventions must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when operations is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when operations is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenOperationsIsNull() {
      // Given
      final @Nullable OperationDefaults nullOperations = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new Configuration(conventions, nullOperations, mockLoader));

      assertEquals("operations must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when loader is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when loader is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenLoaderIsNull() {
      // Given
      final @Nullable DataSetLoader nullLoader = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new Configuration(conventions, operations, nullLoader));

      assertEquals("loader must not be null", exception.getMessage());
    }
  }

  /** Tests for the defaults() factory method. */
  @Nested
  @DisplayName("defaults() method")
  class DefaultsMethod {

    /** Tests for the defaults method. */
    DefaultsMethod() {}

    /** Verifies that defaults method returns configuration with standard values. */
    @Test
    @Tag("normal")
    @DisplayName("should return configuration with standard values")
    void shouldReturnConfiguration_withStandardValues() {
      // When
      final var configuration = Configuration.defaults();

      // Then
      assertAll(
          "default configuration",
          () -> assertNotNull(configuration, "configuration should not be null"),
          () ->
              assertEquals(
                  ConventionSettings.standard(),
                  configuration.conventions(),
                  "conventions should be standard"),
          () ->
              assertEquals(
                  OperationDefaults.standard(),
                  configuration.operations(),
                  "operations should be standard"),
          () -> assertNotNull(configuration.loader(), "loader should not be null"));
    }

    /** Verifies that defaults method returns same loader instance on multiple calls. */
    @Test
    @Tag("normal")
    @DisplayName("should return same loader instance on multiple calls")
    void shouldReturnSameLoaderInstance_onMultipleCalls() {
      // When
      final var configuration1 = Configuration.defaults();
      final var configuration2 = Configuration.defaults();

      // Then
      assertEquals(
          configuration1.loader().getClass(),
          configuration2.loader().getClass(),
          "loader class should be the same");
    }
  }

  /** Tests for the withConventions() factory method. */
  @Nested
  @DisplayName("withConventions() method")
  class WithConventionsMethod {

    /** Tests for the withConventions method. */
    WithConventionsMethod() {}

    /**
     * Verifies that withConventions returns configuration with custom conventions and standard
     * defaults.
     */
    @Test
    @Tag("normal")
    @DisplayName("should return configuration with custom conventions and standard defaults")
    void shouldReturnConfiguration_withCustomConventionsAndStandardDefaults() {
      // Given
      final var customConventions = new ConventionSettings("/custom", "/verify", "[Test]");

      // When
      final var configuration = Configuration.withConventions(customConventions);

      // Then
      assertAll(
          "configuration with custom conventions",
          () -> assertEquals(customConventions, configuration.conventions()),
          () -> assertEquals(OperationDefaults.standard(), configuration.operations()),
          () -> assertNotNull(configuration.loader()));
    }

    /** Verifies that withConventions throws exception when conventions is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when conventions is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenConventionsIsNull() {
      // Given
      final @Nullable ConventionSettings nullConventions = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> Configuration.withConventions(nullConventions));

      assertEquals("conventions must not be null", exception.getMessage());
    }
  }

  /** Tests for the withOperations() factory method. */
  @Nested
  @DisplayName("withOperations() method")
  class WithOperationsMethod {

    /** Tests for the withOperations method. */
    WithOperationsMethod() {}

    /**
     * Verifies that withOperations returns configuration with custom operations and standard
     * defaults.
     */
    @Test
    @Tag("normal")
    @DisplayName("should return configuration with custom operations and standard defaults")
    void shouldReturnConfiguration_withCustomOperationsAndStandardDefaults() {
      // Given
      final var customOperations = mock(OperationDefaults.class);

      // When
      final var configuration = Configuration.withOperations(customOperations);

      // Then
      assertAll(
          "configuration with custom operations",
          () -> assertEquals(ConventionSettings.standard(), configuration.conventions()),
          () -> assertEquals(customOperations, configuration.operations()),
          () -> assertNotNull(configuration.loader()));
    }

    /** Verifies that withOperations throws exception when operations is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when operations is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenOperationsIsNull() {
      // Given
      final @Nullable OperationDefaults nullOperations = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> Configuration.withOperations(nullOperations));

      assertEquals("operations must not be null", exception.getMessage());
    }
  }

  /** Tests for the withLoader() factory method. */
  @Nested
  @DisplayName("withLoader() method")
  class WithLoaderMethod {

    /** Tests for the withLoader method. */
    WithLoaderMethod() {}

    /** Verifies that withLoader returns configuration with custom loader and standard defaults. */
    @Test
    @Tag("normal")
    @DisplayName("should return configuration with custom loader and standard defaults")
    void shouldReturnConfiguration_withCustomLoaderAndStandardDefaults() {
      // When
      final var configuration = Configuration.withLoader(mockLoader);

      // Then
      assertAll(
          "configuration with custom loader",
          () -> assertEquals(ConventionSettings.standard(), configuration.conventions()),
          () -> assertEquals(OperationDefaults.standard(), configuration.operations()),
          () -> assertEquals(mockLoader, configuration.loader()));
    }

    /** Verifies that withLoader throws exception when loader is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when loader is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenLoaderIsNull() {
      // Given
      final @Nullable DataSetLoader nullLoader = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> Configuration.withLoader(nullLoader));

      assertEquals("loader must not be null", exception.getMessage());
    }
  }
}
