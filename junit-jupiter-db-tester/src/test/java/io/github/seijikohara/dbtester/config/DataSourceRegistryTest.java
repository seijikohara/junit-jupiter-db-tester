package io.github.seijikohara.dbtester.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.exception.DataSourceNotFoundException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DataSourceRegistry}. */
@DisplayName("DataSourceRegistry")
class DataSourceRegistryTest {

  /** Mock default data source. */
  private DataSource mockDataSource;

  /** Mock warehouse data source. */
  private DataSource mockWarehouseDataSource;

  /** Data source registry under test. */
  private DataSourceRegistry registry;

  /** Constructs test instance. */
  DataSourceRegistryTest() {}

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    mockDataSource = mock(DataSource.class);
    mockWarehouseDataSource = mock(DataSource.class);
    registry = new DataSourceRegistry();
  }

  /** Tests for registerDefault method. */
  @Nested
  @DisplayName("registerDefault() method")
  class RegisterDefaultMethod {

    /** Constructs test instance. */
    RegisterDefaultMethod() {}

    /** Verifies that default data source is registered. */
    @Test
    @Tag("normal")
    @DisplayName("Registers default data source")
    void registersDefaultDataSource() {
      // When
      registry.registerDefault(mockDataSource);

      // Then
      assertTrue(registry.hasDefault());
      assertEquals(mockDataSource, registry.getDefault());
    }

    /** Verifies that registering a new default replaces the old one. */
    @Test
    @Tag("normal")
    @DisplayName("Replaces existing default data source")
    void replacesExistingDefault() {
      // Given
      registry.registerDefault(mockDataSource);

      // When
      registry.registerDefault(mockWarehouseDataSource);

      // Then
      assertEquals(mockWarehouseDataSource, registry.getDefault());
    }
  }

  /** Tests for register method. */
  @Nested
  @DisplayName("register() method")
  class RegisterMethod {

    /** Constructs test instance. */
    RegisterMethod() {}

    /** Verifies that named data source is registered. */
    @Test
    @Tag("normal")
    @DisplayName("Registers named data source")
    void registersNamedDataSource() {
      // When
      registry.register("warehouse", mockWarehouseDataSource);

      // Then
      assertTrue(registry.has("warehouse"));
      assertEquals(mockWarehouseDataSource, registry.get("warehouse"));
    }

    /** Verifies that empty name registers as default. */
    @Test
    @Tag("edge-case")
    @DisplayName("Registers as default when name is empty")
    void registersAsDefault_whenNameIsEmpty() {
      // When
      registry.register("", mockDataSource);

      // Then
      assertTrue(registry.hasDefault());
      assertEquals(mockDataSource, registry.getDefault());
    }

    /** Verifies that whitespace-only name registers as default. */
    @Test
    @Tag("edge-case")
    @DisplayName("Registers as default when name is whitespace")
    void registersAsDefault_whenNameIsWhitespace() {
      // When
      registry.register("   ", mockDataSource);

      // Then
      assertTrue(registry.hasDefault());
      assertEquals(mockDataSource, registry.getDefault());
    }
  }

  /** Tests for getDefault method. */
  @Nested
  @DisplayName("getDefault() method")
  class GetDefaultMethod {

    /** Constructs test instance. */
    GetDefaultMethod() {}

    /** Verifies that default data source is retrieved. */
    @Test
    @Tag("normal")
    @DisplayName("Returns default data source")
    void returnsDefaultDataSource() {
      // Given
      registry.registerDefault(mockDataSource);

      // When
      final var result = registry.getDefault();

      // Then
      assertEquals(mockDataSource, result);
    }

    /** Verifies that exception is thrown when no default exists. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when no default registered")
    void throwsException_whenNoDefaultRegistered() {
      // When & Then
      final var exception = assertThrows(DataSourceNotFoundException.class, registry::getDefault);
      assertEquals("No default data source registered", exception.getMessage());
    }
  }

  /** Tests for get method. */
  @Nested
  @DisplayName("get() method")
  class GetMethod {

    /** Constructs test instance. */
    GetMethod() {}

    /** Verifies that named data source is retrieved. */
    @Test
    @Tag("normal")
    @DisplayName("Returns named data source")
    void returnsNamedDataSource() {
      // Given
      registry.register("warehouse", mockWarehouseDataSource);

      // When
      final var result = registry.get("warehouse");

      // Then
      assertEquals(mockWarehouseDataSource, result);
    }

    /** Verifies that default is returned when name is null. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns default when name is null")
    void returnsDefault_whenNameIsNull() {
      // Given
      registry.registerDefault(mockDataSource);

      // When
      final var result = registry.get(null);

      // Then
      assertEquals(mockDataSource, result);
    }

    /** Verifies that default is returned when name is empty. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns default when name is empty")
    void returnsDefault_whenNameIsEmpty() {
      // Given
      registry.registerDefault(mockDataSource);

      // When
      final var result = registry.get("");

      // Then
      assertEquals(mockDataSource, result);
    }

    /** Verifies that exception is thrown when named data source not found. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when named data source not found")
    void throwsException_whenNamedDataSourceNotFound() {
      // When & Then
      final var exception =
          assertThrows(DataSourceNotFoundException.class, () -> registry.get("nonexistent"));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("nonexistent"));
    }
  }

  /** Tests for find method. */
  @Nested
  @DisplayName("find() method")
  class FindMethod {

    /** Constructs test instance. */
    FindMethod() {}

    /** Verifies that named data source is found. */
    @Test
    @Tag("normal")
    @DisplayName("Returns present Optional when data source exists")
    void returnsPresent_whenDataSourceExists() {
      // Given
      registry.register("warehouse", mockWarehouseDataSource);

      // When
      final var result = registry.find("warehouse");

      // Then
      assertTrue(result.isPresent());
      result.ifPresent(ds -> assertEquals(mockWarehouseDataSource, ds));
    }

    /** Verifies that empty Optional is returned when not found. */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty Optional when data source not found")
    void returnsEmpty_whenDataSourceNotFound() {
      // When
      final var result = registry.find("nonexistent");

      // Then
      assertTrue(result.isEmpty());
    }
  }

  /** Tests for has and hasDefault methods. */
  @Nested
  @DisplayName("has() and hasDefault() methods")
  class HasMethods {

    /** Constructs test instance. */
    HasMethods() {}

    /** Verifies that hasDefault returns true when default exists. */
    @Test
    @Tag("normal")
    @DisplayName("hasDefault returns true when default exists")
    void hasDefaultReturnsTrue_whenDefaultExists() {
      // Given
      registry.registerDefault(mockDataSource);

      // When & Then
      assertTrue(registry.hasDefault());
    }

    /** Verifies that hasDefault returns false when no default. */
    @Test
    @Tag("normal")
    @DisplayName("hasDefault returns false when no default")
    void hasDefaultReturnsFalse_whenNoDefault() {
      // When & Then
      assertFalse(registry.hasDefault());
    }

    /** Verifies that has returns true when named data source exists. */
    @Test
    @Tag("normal")
    @DisplayName("has returns true when named data source exists")
    void hasReturnsTrue_whenNamedDataSourceExists() {
      // Given
      registry.register("warehouse", mockWarehouseDataSource);

      // When & Then
      assertTrue(registry.has("warehouse"));
    }

    /** Verifies that has returns false when named data source does not exist. */
    @Test
    @Tag("normal")
    @DisplayName("has returns false when named data source does not exist")
    void hasReturnsFalse_whenNamedDataSourceDoesNotExist() {
      // When & Then
      assertFalse(registry.has("nonexistent"));
    }
  }

  /** Tests for clear method. */
  @Nested
  @DisplayName("clear() method")
  class ClearMethod {

    /** Constructs test instance. */
    ClearMethod() {}

    /** Verifies that clear removes all data sources. */
    @Test
    @Tag("normal")
    @DisplayName("Removes all data sources including default")
    void removesAllDataSources() {
      // Given
      registry.registerDefault(mockDataSource);
      registry.register("warehouse", mockWarehouseDataSource);

      // When
      registry.clear();

      // Then
      assertFalse(registry.hasDefault());
      assertFalse(registry.has("warehouse"));
    }
  }
}
