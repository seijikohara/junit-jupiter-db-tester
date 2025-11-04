package io.github.seijikohara.dbtester.api.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.seijikohara.dbtester.api.exception.DataSourceNotFoundException;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSourceRegistry}. */
@DisplayName("DataSourceRegistry")
class DataSourceRegistryTest {

  /** Tests for the DataSourceRegistry class. */
  DataSourceRegistryTest() {}

  /** Registry instance for testing. */
  private DataSourceRegistry registry;

  /** Mock data source for testing. */
  private DataSource mockDataSource1;

  /** Second mock data source for testing. */
  private DataSource mockDataSource2;

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    registry = new DataSourceRegistry();
    mockDataSource1 = mock(DataSource.class);
    mockDataSource2 = mock(DataSource.class);
  }

  /** Tests for the registerDefault(DataSource) method. */
  @Nested
  @DisplayName("registerDefault(DataSource) method")
  class RegisterDefaultMethod {

    /** Tests for the registerDefault method. */
    RegisterDefaultMethod() {}

    /** Verifies that registerDefault successfully registers a default data source. */
    @Test
    @Tag("normal")
    @DisplayName("should register default data source when valid data source provided")
    void shouldRegisterDefaultDataSource_whenValidDataSourceProvided() {
      // When
      registry.registerDefault(mockDataSource1);

      // Then
      assertAll(
          "default data source registration",
          () -> assertTrue(registry.hasDefault(), "hasDefault should return true"),
          () -> assertSame(mockDataSource1, registry.getDefault(), "should return same instance"));
    }

    /** Verifies that registerDefault throws exception when data source is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when data source is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenDataSourceIsNull() {
      // Given
      final @Nullable DataSource nullDataSource = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> registry.registerDefault(nullDataSource));

      assertEquals("dataSource must not be null", exception.getMessage());
    }

    /**
     * Verifies that registerDefault replaces existing default data source when called multiple
     * times.
     */
    @Test
    @Tag("edge-case")
    @DisplayName("should replace default data source when called multiple times")
    void shouldReplaceDefaultDataSource_whenCalledMultipleTimes() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      registry.registerDefault(mockDataSource2);

      // Then
      assertSame(
          mockDataSource2,
          registry.getDefault(),
          "should return the most recently registered data source");
    }

    /**
     * Verifies that default data source is available via multiple access methods after
     * registration.
     */
    @Test
    @Tag("normal")
    @DisplayName("should make default available via multiple methods after registration")
    void shouldMakeDefaultAvailableViaMultipleMethods_afterRegistration() {
      // When
      registry.registerDefault(mockDataSource1);

      // Then
      assertAll(
          "default data source accessibility",
          () -> assertSame(mockDataSource1, registry.getDefault(), "via getDefault()"),
          () -> assertSame(mockDataSource1, registry.get(null), "via get(null)"),
          () -> assertSame(mockDataSource1, registry.get(""), "via get(empty string)"));
    }
  }

  /** Tests for the register(String, DataSource) method. */
  @Nested
  @DisplayName("register(String, DataSource) method")
  class RegisterMethod {

    /** Tests for the register method. */
    RegisterMethod() {}

    /** Verifies that register successfully registers a named data source. */
    @Test
    @Tag("normal")
    @DisplayName("should register named data source when valid name and data source provided")
    void shouldRegisterNamedDataSource_whenValidNameAndDataSourceProvided() {
      // Given
      final var name = "testDataSource";

      // When
      registry.register(name, mockDataSource1);

      // Then
      assertAll(
          "named data source registration",
          () -> assertTrue(registry.has(name), "has() should return true"),
          () ->
              assertTrue(registry.find(name).isPresent(), "find() should return present Optional"),
          () ->
              assertSame(mockDataSource1, registry.get(name), "get() should return same instance"));
    }

    /** Verifies that register throws exception when name is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when name is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenNameIsNull() {
      // Given
      final @Nullable String nullName = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class, () -> registry.register(nullName, mockDataSource1));

      assertEquals("name must not be null", exception.getMessage());
    }

    /** Verifies that register throws exception when data source is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when data source is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenDataSourceIsNull() {
      // Given
      final @Nullable DataSource nullDataSource = null;

      // When & Then
      final var exception =
          assertThrows(NullPointerException.class, () -> registry.register("test", nullDataSource));

      assertEquals("dataSource must not be null", exception.getMessage());
    }

    /** Verifies that register registers as default when name is empty string. */
    @Test
    @Tag("edge-case")
    @DisplayName("should register as default when name is empty")
    void shouldRegisterAsDefault_whenNameIsEmpty() {
      // When
      registry.register("", mockDataSource1);

      // Then
      assertAll(
          "empty name registration",
          () -> assertTrue(registry.hasDefault(), "should register as default"),
          () ->
              assertSame(
                  mockDataSource1, registry.getDefault(), "should be accessible as default"));
    }

    /** Verifies that register registers as default when name is whitespace only. */
    @Test
    @Tag("edge-case")
    @DisplayName("should register as default when name is whitespace only")
    void shouldRegisterAsDefault_whenNameIsWhitespaceOnly() {
      // When
      registry.register("   ", mockDataSource1);

      // Then
      assertAll(
          "whitespace name registration",
          () -> assertTrue(registry.hasDefault(), "should register as default"),
          () ->
              assertSame(
                  mockDataSource1, registry.getDefault(), "should be accessible as default"));
    }

    /** Verifies that register replaces data source when same name used multiple times. */
    @Test
    @Tag("edge-case")
    @DisplayName("should replace data source when same name used multiple times")
    void shouldReplaceDataSource_whenSameNameUsedMultipleTimes() {
      // Given
      final var name = "testDataSource";
      registry.register(name, mockDataSource1);

      // When
      registry.register(name, mockDataSource2);

      // Then
      assertSame(
          mockDataSource2,
          registry.get(name),
          "should return the most recently registered data source");
    }
  }

  /** Tests for the getDefault() method. */
  @Nested
  @DisplayName("getDefault() method")
  class GetDefaultMethod {

    /** Tests for the getDefault method. */
    GetDefaultMethod() {}

    /** Verifies that getDefault returns default data source when default is registered. */
    @Test
    @Tag("normal")
    @DisplayName("should return default data source when default is registered")
    void shouldReturnDefaultDataSource_whenDefaultIsRegistered() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      final var result = registry.getDefault();

      // Then
      assertSame(mockDataSource1, result);
    }

    /** Verifies that getDefault throws exception when no default registered. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when no default registered")
    void shouldThrowException_whenNoDefaultRegistered() {
      // When & Then
      final var exception =
          assertThrows(DataSourceNotFoundException.class, () -> registry.getDefault());

      assertEquals("No default data source registered", exception.getMessage());
    }

    /** Verifies that getDefault throws exception when cleared after registration. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when cleared after registration")
    void shouldThrowException_whenClearedAfterRegistration() {
      // Given
      registry.registerDefault(mockDataSource1);
      registry.clear();

      // When & Then
      final var exception =
          assertThrows(DataSourceNotFoundException.class, () -> registry.getDefault());

      assertEquals("No default data source registered", exception.getMessage());
    }
  }

  /** Tests for the get(@Nullable String) method. */
  @Nested
  @DisplayName("get(@Nullable String) method")
  class GetMethod {

    /** Tests for the get method. */
    GetMethod() {}

    /** Verifies that get returns named data source when name is registered. */
    @Test
    @Tag("normal")
    @DisplayName("should return named data source when name is registered")
    void shouldReturnNamedDataSource_whenNameIsRegistered() {
      // Given
      final var name = "testDataSource";
      registry.register(name, mockDataSource1);

      // When
      final var result = registry.get(name);

      // Then
      assertSame(mockDataSource1, result);
    }

    /** Verifies that get returns default data source when name is null. */
    @Test
    @Tag("normal")
    @DisplayName("should return default data source when name is null")
    @SuppressWarnings("NullAway")
    void shouldReturnDefaultDataSource_whenNameIsNull() {
      // Given
      registry.registerDefault(mockDataSource1);
      final @Nullable String nullName = null;

      // When
      final var result = registry.get(nullName);

      // Then
      assertSame(mockDataSource1, result);
    }

    /** Verifies that get returns default data source when name is empty. */
    @Test
    @Tag("normal")
    @DisplayName("should return default data source when name is empty")
    void shouldReturnDefaultDataSource_whenNameIsEmpty() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      final var result = registry.get("");

      // Then
      assertSame(mockDataSource1, result);
    }

    /** Verifies that get throws exception when named data source not found. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when named data source not found")
    void shouldThrowException_whenNamedDataSourceNotFound() {
      // Given
      final var name = "nonexistent";

      // When & Then
      final var exception =
          assertThrows(DataSourceNotFoundException.class, () -> registry.get(name));

      assertEquals("No data source registered for name: nonexistent", exception.getMessage());
    }

    /** Verifies that get throws exception when no default and name is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when no default and name is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenNoDefaultAndNameIsNull() {
      // Given
      final @Nullable String nullName = null;

      // When & Then
      final var exception =
          assertThrows(DataSourceNotFoundException.class, () -> registry.get(nullName));

      assertEquals("No default data source registered", exception.getMessage());
    }

    /** Verifies that get returns named data source when both named and default exist. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return named data source when both named and default exist")
    void shouldReturnNamedDataSource_whenBothNamedAndDefaultExist() {
      // Given
      registry.registerDefault(mockDataSource1);
      final var name = "named";
      registry.register(name, mockDataSource2);

      // When
      final var result = registry.get(name);

      // Then
      assertSame(mockDataSource2, result, "should prioritize named data source over default");
    }
  }

  /** Tests for the find(String) method. */
  @Nested
  @DisplayName("find(String) method")
  class FindMethod {

    /** Tests for the find method. */
    FindMethod() {}

    /** Verifies that find returns present Optional when named data source exists. */
    @Test
    @Tag("normal")
    @DisplayName("should return present Optional when named data source exists")
    void shouldReturnPresentOptional_whenNamedDataSourceExists() {
      // Given
      final var name = "testDataSource";
      registry.register(name, mockDataSource1);

      // When
      final var result = registry.find(name);

      // Then
      assertTrue(result.isPresent(), "Optional should be present");
    }

    /** Verifies that find returns empty Optional when named data source not found. */
    @Test
    @Tag("normal")
    @DisplayName("should return empty Optional when named data source not found")
    void shouldReturnEmptyOptional_whenNamedDataSourceNotFound() {
      // When
      final var result = registry.find("nonexistent");

      // Then
      assertTrue(result.isEmpty(), "Optional should be empty");
    }

    /** Verifies that find throws exception when name is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when name is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenNameIsNull() {
      // Given
      final @Nullable String nullName = null;

      // When & Then
      final var exception = assertThrows(NullPointerException.class, () -> registry.find(nullName));

      assertEquals("name must not be null", exception.getMessage());
    }

    /** Verifies that find returns empty Optional when only default exists. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return empty Optional when only default exists")
    void shouldReturnEmptyOptional_whenOnlyDefaultExists() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      final var result = registry.find("anyName");

      // Then
      assertTrue(result.isEmpty(), "find() should not fall back to default");
    }
  }

  /** Tests for the hasDefault() method. */
  @Nested
  @DisplayName("hasDefault() method")
  class HasDefaultMethod {

    /** Tests for the hasDefault method. */
    HasDefaultMethod() {}

    /** Verifies that hasDefault returns true when default is registered. */
    @Test
    @Tag("normal")
    @DisplayName("should return true when default is registered")
    void shouldReturnTrue_whenDefaultIsRegistered() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      final var result = registry.hasDefault();

      // Then
      assertTrue(result);
    }

    /** Verifies that hasDefault returns false when no default registered. */
    @Test
    @Tag("normal")
    @DisplayName("should return false when no default registered")
    void shouldReturnFalse_whenNoDefaultRegistered() {
      // When
      final var result = registry.hasDefault();

      // Then
      assertFalse(result);
    }

    /** Verifies that hasDefault returns false when cleared after registration. */
    @Test
    @Tag("normal")
    @DisplayName("should return false when cleared after registration")
    void shouldReturnFalse_whenClearedAfterRegistration() {
      // Given
      registry.registerDefault(mockDataSource1);
      registry.clear();

      // When
      final var result = registry.hasDefault();

      // Then
      assertFalse(result);
    }
  }

  /** Tests for the has(String) method. */
  @Nested
  @DisplayName("has(String) method")
  class HasMethod {

    /** Tests for the has method. */
    HasMethod() {}

    /** Verifies that has returns true when named data source exists. */
    @Test
    @Tag("normal")
    @DisplayName("should return true when named data source exists")
    void shouldReturnTrue_whenNamedDataSourceExists() {
      // Given
      final var name = "testDataSource";
      registry.register(name, mockDataSource1);

      // When
      final var result = registry.has(name);

      // Then
      assertTrue(result);
    }

    /** Verifies that has returns false when named data source not found. */
    @Test
    @Tag("normal")
    @DisplayName("should return false when named data source not found")
    void shouldReturnFalse_whenNamedDataSourceNotFound() {
      // When
      final var result = registry.has("nonexistent");

      // Then
      assertFalse(result);
    }

    /** Verifies that has throws exception when name is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when name is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenNameIsNull() {
      // Given
      final @Nullable String nullName = null;

      // When & Then
      final var exception = assertThrows(NullPointerException.class, () -> registry.has(nullName));

      assertEquals("name must not be null", exception.getMessage());
    }

    /** Verifies that has returns false when only default exists. */
    @Test
    @Tag("edge-case")
    @DisplayName("should return false when only default exists")
    void shouldReturnFalse_whenOnlyDefaultExists() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      final var result = registry.has("anyName");

      // Then
      assertFalse(result, "has() should not consider default data source");
    }
  }

  /** Tests for the clear() method. */
  @Nested
  @DisplayName("clear() method")
  class ClearMethod {

    /** Tests for the clear method. */
    ClearMethod() {}

    /** Verifies that clear removes all data sources when called. */
    @Test
    @Tag("normal")
    @DisplayName("should remove all data sources when called")
    void shouldRemoveAllDataSources_whenCalled() {
      // Given
      registry.registerDefault(mockDataSource1);
      registry.register("named", mockDataSource2);

      // When
      registry.clear();

      // Then
      assertAll(
          "all data sources removed",
          () -> assertFalse(registry.hasDefault(), "default should be removed"),
          () -> assertFalse(registry.has("named"), "named data source should be removed"),
          () ->
              assertThrows(
                  DataSourceNotFoundException.class,
                  () -> registry.getDefault(),
                  "getDefault() should throw exception"),
          () ->
              assertThrows(
                  DataSourceNotFoundException.class,
                  () -> registry.get("named"),
                  "get(named) should throw exception"));
    }

    /** Verifies that clear does nothing when registry is empty. */
    @Test
    @Tag("normal")
    @DisplayName("should do nothing when registry is empty")
    void shouldDoNothing_whenRegistryIsEmpty() {
      // When
      registry.clear();

      // Then
      assertAll(
          "registry still empty",
          () -> assertFalse(registry.hasDefault(), "default should still be absent"),
          () ->
              assertThrows(
                  DataSourceNotFoundException.class,
                  () -> registry.getDefault(),
                  "getDefault() should still throw exception"));
    }

    /** Verifies that clear removes default data source when only default exists. */
    @Test
    @Tag("normal")
    @DisplayName("should remove default data source when only default exists")
    void shouldRemoveDefaultDataSource_whenOnlyDefaultExists() {
      // Given
      registry.registerDefault(mockDataSource1);

      // When
      registry.clear();

      // Then
      assertFalse(registry.hasDefault(), "default should be removed");
    }

    /** Verifies that clear removes named data sources when only named exist. */
    @Test
    @Tag("normal")
    @DisplayName("should remove named data sources when only named exist")
    void shouldRemoveNamedDataSources_whenOnlyNamedExist() {
      // Given
      registry.register("named1", mockDataSource1);
      registry.register("named2", mockDataSource2);

      // When
      registry.clear();

      // Then
      assertAll(
          "all named data sources removed",
          () -> assertFalse(registry.has("named1"), "named1 should be removed"),
          () -> assertFalse(registry.has("named2"), "named2 should be removed"));
    }
  }
}
