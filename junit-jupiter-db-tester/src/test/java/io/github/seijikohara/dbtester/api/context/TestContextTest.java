package io.github.seijikohara.dbtester.api.context;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.seijikohara.dbtester.api.config.Configuration;
import io.github.seijikohara.dbtester.api.config.DataSourceRegistry;
import java.lang.reflect.Method;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Unit tests for {@link TestContext}. */
@DisplayName("TestContext")
class TestContextTest {

  /** Tests for the TestContext record. */
  TestContextTest() {}

  /** Mock extension context for testing. */
  private ExtensionContext mockExtensionContext;

  /** Test class for testing. */
  private Class<?> testClass;

  /** Test method for testing. */
  private Method testMethod;

  /** Configuration instance for testing. */
  private Configuration configuration;

  /** Registry instance for testing. */
  private DataSourceRegistry registry;

  /**
   * Sets up test fixtures before each test.
   *
   * @throws NoSuchMethodException if test method cannot be found
   */
  @BeforeEach
  void setUp() throws NoSuchMethodException {
    mockExtensionContext = mock(ExtensionContext.class);
    testClass = TestContextTest.class;
    testMethod = TestContextTest.class.getDeclaredMethod("setUp");
    configuration = Configuration.defaults();
    registry = new DataSourceRegistry();
  }

  /** Tests for the TestContext constructor. */
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
      final var context = new TestContext(testClass, testMethod, configuration, registry);

      // Then
      assertAll(
          "test context components",
          () -> assertSame(testClass, context.testClass()),
          () -> assertSame(testMethod, context.testMethod()),
          () -> assertSame(configuration, context.configuration()),
          () -> assertSame(registry, context.registry()));
    }

    /** Verifies that constructor throws exception when testClass is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when testClass is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenTestClassIsNull() {
      // Given
      final @Nullable Class<?> nullTestClass = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new TestContext(nullTestClass, testMethod, configuration, registry));

      assertEquals("testClass must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when testMethod is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when testMethod is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenTestMethodIsNull() {
      // Given
      final @Nullable Method nullTestMethod = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new TestContext(testClass, nullTestMethod, configuration, registry));

      assertEquals("testMethod must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when configuration is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when configuration is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenConfigurationIsNull() {
      // Given
      final @Nullable Configuration nullConfiguration = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new TestContext(testClass, testMethod, nullConfiguration, registry));

      assertEquals("configuration must not be null", exception.getMessage());
    }

    /** Verifies that constructor throws exception when registry is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when registry is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenRegistryIsNull() {
      // Given
      final @Nullable DataSourceRegistry nullRegistry = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> new TestContext(testClass, testMethod, configuration, nullRegistry));

      assertEquals("registry must not be null", exception.getMessage());
    }
  }

  /** Tests for the from(ExtensionContext, Configuration, DataSourceRegistry) factory method. */
  @Nested
  @DisplayName("from(ExtensionContext, Configuration, DataSourceRegistry) method")
  class FromMethod {

    /** Tests for the from factory method. */
    FromMethod() {}

    /** Verifies that from creates TestContext with values from ExtensionContext. */
    @Test
    @Tag("normal")
    @DisplayName("should create TestContext with values from ExtensionContext")
    @SuppressWarnings("unchecked")
    void shouldCreateTestContext_withValuesFromExtensionContext() {
      // Given
      when(mockExtensionContext.getRequiredTestClass()).thenReturn((Class) testClass);
      when(mockExtensionContext.getRequiredTestMethod()).thenReturn(testMethod);

      // When
      final var context = TestContext.from(mockExtensionContext, configuration, registry);

      // Then
      assertAll(
          "test context from extension context",
          () -> assertNotNull(context),
          () -> assertSame(testClass, context.testClass()),
          () -> assertSame(testMethod, context.testMethod()),
          () -> assertSame(configuration, context.configuration()),
          () -> assertSame(registry, context.registry()));
    }

    /** Verifies that from throws exception when extensionContext is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when extensionContext is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenExtensionContextIsNull() {
      // Given
      final @Nullable ExtensionContext nullExtensionContext = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> TestContext.from(nullExtensionContext, configuration, registry));

      assertEquals("extensionContext must not be null", exception.getMessage());
    }

    /** Verifies that from throws exception when configuration is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when configuration is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenConfigurationIsNull() {
      // Given
      final @Nullable Configuration nullConfiguration = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> TestContext.from(mockExtensionContext, nullConfiguration, registry));

      assertEquals("configuration must not be null", exception.getMessage());
    }

    /** Verifies that from throws exception when registry is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw exception when registry is null")
    @SuppressWarnings("NullAway")
    void shouldThrowException_whenRegistryIsNull() {
      // Given
      final @Nullable DataSourceRegistry nullRegistry = null;

      // When & Then
      final var exception =
          assertThrows(
              NullPointerException.class,
              () -> TestContext.from(mockExtensionContext, configuration, nullRegistry));

      assertEquals("registry must not be null", exception.getMessage());
    }
  }
}
