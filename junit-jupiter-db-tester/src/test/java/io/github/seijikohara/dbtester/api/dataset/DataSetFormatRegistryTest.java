package io.github.seijikohara.dbtester.api.dataset;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.seijikohara.dbtester.api.domain.FileExtension;
import io.github.seijikohara.dbtester.api.exception.ConfigurationException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSetFormatRegistry}. */
@DisplayName("DataSetFormatRegistry")
class DataSetFormatRegistryTest {

  /** Tests for the DataSetFormatRegistry class. */
  DataSetFormatRegistryTest() {}

  /** Tests for the register(DataSetFormatProvider) method. */
  @Nested
  @DisplayName("register(DataSetFormatProvider) method")
  class RegisterMethod {

    /** Tests for the register method. */
    RegisterMethod() {}

    /** Verifies that register stores provider and makes it retrievable. */
    @Test
    @Tag("normal")
    @DisplayName("should register provider when provider is valid")
    void shouldRegisterProvider_whenProviderIsValid() {
      // Given
      final var extension = new FileExtension(".test1");
      final var provider = mock(DataSetFormatProvider.class);
      when(provider.supportedFileExtension()).thenReturn(extension);

      // When
      DataSetFormatRegistry.register(provider);

      // Then
      final var retrieved = DataSetFormatRegistry.getProvider(extension);
      assertSame(provider, retrieved, "should retrieve the registered provider");
    }

    /** Verifies that register replaces existing provider for same extension. */
    @Test
    @Tag("normal")
    @DisplayName("should replace provider when extension already registered")
    void shouldReplaceProvider_whenExtensionAlreadyRegistered() {
      // Given
      final var extension = new FileExtension(".test2");
      final var provider1 = mock(DataSetFormatProvider.class);
      final var provider2 = mock(DataSetFormatProvider.class);
      when(provider1.supportedFileExtension()).thenReturn(extension);
      when(provider2.supportedFileExtension()).thenReturn(extension);

      // When
      DataSetFormatRegistry.register(provider1);
      DataSetFormatRegistry.register(provider2);

      // Then
      final var retrieved = DataSetFormatRegistry.getProvider(extension);
      assertSame(provider2, retrieved, "should retrieve the last registered provider");
    }

    /** Verifies that register throws exception when provider is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when provider is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenProviderIsNull() {
      // Given
      final @Nullable DataSetFormatProvider nullProvider = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> DataSetFormatRegistry.register(nullProvider));

      assertEquals("provider must not be null", exception.getMessage());
    }

    /** Verifies that register throws exception when supportedFileExtension returns null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when supportedFileExtension is null")
    void shouldThrowException_whenSupportedFileExtensionIsNull() {
      // Given
      final var provider = mock(DataSetFormatProvider.class);
      when(provider.supportedFileExtension()).thenReturn(null);

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> DataSetFormatRegistry.register(provider));

      assertEquals("supportedFileExtension must not be null", exception.getMessage());
    }
  }

  /** Tests for the getProvider(FileExtension) method. */
  @Nested
  @DisplayName("getProvider(FileExtension) method")
  class GetProviderMethod {

    /** Tests for the getProvider method. */
    GetProviderMethod() {}

    /** Verifies that getProvider returns registered provider. */
    @Test
    @Tag("normal")
    @DisplayName("should return provider when extension is registered")
    void shouldReturnProvider_whenExtensionIsRegistered() {
      // Given
      final var extension = new FileExtension(".test3");
      final var provider = mock(DataSetFormatProvider.class);
      when(provider.supportedFileExtension()).thenReturn(extension);
      DataSetFormatRegistry.register(provider);

      // When
      final var result = DataSetFormatRegistry.getProvider(extension);

      // Then
      assertSame(provider, result, "should return the registered provider");
    }

    /** Verifies that getProvider returns CSV provider from ServiceLoader. */
    @Test
    @Tag("normal")
    @DisplayName("should return CSV provider from ServiceLoader")
    void shouldReturnCsvProvider_fromServiceLoader() {
      // Given
      final var csvExtension = new FileExtension(".csv");

      // When
      final var provider = DataSetFormatRegistry.getProvider(csvExtension);

      // Then
      assertAll(
          "CSV provider from ServiceLoader",
          () -> assertEquals(csvExtension, provider.supportedFileExtension()),
          () -> assertTrue(provider.getClass().getSimpleName().contains("Csv")));
    }

    /** Verifies that getProvider throws exception when fileExtension is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when fileExtension is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenFileExtensionIsNull() {
      // Given
      final @Nullable FileExtension nullExtension = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> DataSetFormatRegistry.getProvider(nullExtension));

      assertEquals("fileExtension must not be null", exception.getMessage());
    }

    /** Verifies that getProvider throws exception when extension not registered. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when extension not registered")
    void shouldThrowException_whenExtensionNotRegistered() {
      // Given
      final var unregisteredExtension = new FileExtension(".nonexistent");

      // When & Then
      assertThrows(
          ConfigurationException.class,
          () -> DataSetFormatRegistry.getProvider(unregisteredExtension));
    }

    /** Verifies that getProvider includes extension info in exception message. */
    @Test
    @Tag("error")
    @DisplayName("should include extension info in exception when extension not registered")
    void shouldIncludeExtensionInfoInException_whenExtensionNotRegistered() {
      // Given
      final var unregisteredExtension = new FileExtension(".nonexistent2");

      // When & Then
      final var exception =
          assertThrows(
              ConfigurationException.class,
              () -> DataSetFormatRegistry.getProvider(unregisteredExtension));

      final var message = exception.getMessage();
      assertAll(
          "exception message should contain extension info",
          () ->
              assertTrue(
                  message != null && message.contains(".nonexistent2"),
                  "should contain the unregistered extension"),
          () ->
              assertTrue(
                  message != null && message.contains("Registered file extensions"),
                  "should mention registered file extensions"));
    }
  }

  /** Tests for the getSupportedExtensions() method. */
  @Nested
  @DisplayName("getSupportedExtensions() method")
  class GetSupportedExtensionsMethod {

    /** Tests for the getSupportedExtensions method. */
    GetSupportedExtensionsMethod() {}

    /** Verifies that getSupportedExtensions returns all registered extensions. */
    @Test
    @Tag("normal")
    @DisplayName("should return all registered extensions")
    void shouldReturnAllRegisteredExtensions() {
      // When
      final var extensions = DataSetFormatRegistry.getSupportedExtensions();

      // Then
      assertTrue(extensions.contains(".csv"), "should contain .csv from ServiceLoader");
    }

    /** Verifies that getSupportedExtensions includes newly registered extension. */
    @Test
    @Tag("normal")
    @DisplayName("should include new extension after registration")
    void shouldIncludeNewExtension_afterRegistration() {
      // Given
      final var extension = new FileExtension(".test4");
      final var provider = mock(DataSetFormatProvider.class);
      when(provider.supportedFileExtension()).thenReturn(extension);

      // When
      DataSetFormatRegistry.register(provider);
      final var extensions = DataSetFormatRegistry.getSupportedExtensions();

      // Then
      assertTrue(extensions.contains(".test4"), "should contain the newly registered extension");
    }

    /** Verifies that getSupportedExtensions returns unmodifiable set. */
    @Test
    @Tag("normal")
    @DisplayName("should return unmodifiable set")
    void shouldReturnUnmodifiableSet() {
      // When
      final var extensions = DataSetFormatRegistry.getSupportedExtensions();

      // Then
      assertThrows(
          UnsupportedOperationException.class,
          () -> extensions.add(".invalid"),
          "returned set should be unmodifiable");
    }
  }
}
