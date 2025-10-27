package io.github.seijikohara.dbtester.internal.dataset.scenario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.exception.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSetFormatRegistry}. */
@DisplayName("DataSetFormatRegistry")
class DataSetFormatRegistryTest {

  /** Constructs test instance. */
  DataSetFormatRegistryTest() {}

  /** Tests for getSupportedExtensions method. */
  @Nested
  @DisplayName("getSupportedExtensions() method")
  class GetSupportedExtensionsMethod {

    /** Constructs test instance. */
    GetSupportedExtensionsMethod() {}

    /** Verifies that supported extensions are returned. */
    @Test
    @Tag("normal")
    @DisplayName("Returns registered extensions")
    void returnsRegisteredExtensions() {
      // When
      final var extensions = DataSetFormatRegistry.getSupportedExtensions();

      // Then
      assertNotNull(extensions);
      assertFalse(extensions.isEmpty());
      // CSV provider should be auto-registered
      assertTrue(extensions.contains(".csv"));
    }

    /** Verifies that returned set is immutable. */
    @Test
    @Tag("normal")
    @DisplayName("Returns immutable set")
    void returnsImmutableSet() {
      // When
      final var extensions = DataSetFormatRegistry.getSupportedExtensions();

      // Then
      assertThrows(UnsupportedOperationException.class, () -> extensions.add(".test"));
    }
  }

  /** Tests for register method. */
  @Nested
  @DisplayName("register() method")
  class RegisterMethod {

    /** Test provider instance. */
    private DataSetFormatProvider testProvider;

    /** Constructs test instance. */
    RegisterMethod() {}

    /** Sets up test fixtures before each test. */
    @BeforeEach
    void setUp() {
      testProvider = mock(DataSetFormatProvider.class);
      when(testProvider.supportedExtension()).thenReturn(".mocktest");
    }

    /** Verifies that provider is registered. */
    @Test
    @Tag("normal")
    @DisplayName("Registers provider successfully")
    void registersProvider() {
      // When
      DataSetFormatRegistry.register(testProvider);

      // Then
      final var provider = DataSetFormatRegistry.getProvider(".mocktest");
      assertEquals(testProvider, provider);
    }

    /** Verifies that existing provider is replaced. */
    @Test
    @Tag("normal")
    @DisplayName("Replaces existing provider")
    void replacesExistingProvider() {
      // Given
      DataSetFormatRegistry.register(testProvider);
      final var newProvider = mock(DataSetFormatProvider.class);
      when(newProvider.supportedExtension()).thenReturn(".mocktest");

      // When
      DataSetFormatRegistry.register(newProvider);

      // Then
      final var provider = DataSetFormatRegistry.getProvider(".mocktest");
      assertEquals(newProvider, provider);
      assertNotEquals(testProvider, provider);
    }
  }

  /** Tests for getProvider method. */
  @Nested
  @DisplayName("getProvider() method")
  class GetProviderMethod {

    /** Constructs test instance. */
    GetProviderMethod() {}

    /** Verifies that registered provider is returned. */
    @Test
    @Tag("normal")
    @DisplayName("Returns registered provider")
    void returnsRegisteredProvider() {
      // When
      final var provider = DataSetFormatRegistry.getProvider(".csv");

      // Then
      assertNotNull(provider);
      assertEquals(".csv", provider.supportedExtension());
    }

    /** Verifies that exception is thrown for unregistered extension. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when extension not registered")
    void throwsException_whenExtensionNotRegistered() {
      // When & Then
      final var exception =
          assertThrows(
              ConfigurationException.class,
              () -> DataSetFormatRegistry.getProvider(".nonexistent"));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains(".nonexistent"));
      assertTrue(exception.getMessage().contains("No format provider registered"));
    }
  }
}
