package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

import io.github.seijikohara.dbtester.config.Configuration;
import io.github.seijikohara.dbtester.config.DataSourceRegistry;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Unit tests for {@link TestContext}. */
@DisplayName("TestContext")
class TestContextTest {

  /** Constructs test instance. */
  TestContextTest() {}

  /** Tests for from() factory method. */
  @Nested
  @DisplayName("from() method")
  class FromMethod {

    /** Constructs test instance. */
    FromMethod() {}

    /**
     * Verifies that from() creates TestContext from ExtensionContext.
     *
     * @throws NoSuchMethodException if test method not found
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates TestContext from ExtensionContext")
    void createsTestContext_fromExtensionContext() throws NoSuchMethodException {
      // Given
      final var extensionContext = mock(ExtensionContext.class);
      final Class<?> testClass = TestContextTest.class;
      final var testMethod = TestContextTest.class.getDeclaredMethod("testMethod");
      final var configuration = mock(Configuration.class);
      final var registry = mock(DataSourceRegistry.class);

      doReturn(testClass).when(extensionContext).getRequiredTestClass();
      doReturn(testMethod).when(extensionContext).getRequiredTestMethod();

      // When
      final var context = TestContext.from(extensionContext, configuration, registry);

      // Then
      assertNotNull(context);
      assertEquals(testClass, context.testClass());
      assertEquals(testMethod, context.testMethod());
      assertEquals(configuration, context.configuration());
      assertEquals(registry, context.registry());
    }

    /** Verifies that from() invokes ExtensionContext methods correctly. */
    @Test
    @Tag("normal")
    @DisplayName("Invokes ExtensionContext methods to retrieve test metadata")
    void invokesExtensionContextMethods() {
      // Given
      final var extensionContext = mock(ExtensionContext.class);
      final Class<?> testClass = TestContextTest.class;
      final var testMethod = mock(Method.class);
      final var configuration = mock(Configuration.class);
      final var registry = mock(DataSourceRegistry.class);

      doReturn(testClass).when(extensionContext).getRequiredTestClass();
      doReturn(testMethod).when(extensionContext).getRequiredTestMethod();

      // When
      TestContext.from(extensionContext, configuration, registry);

      // Then
      verify(extensionContext).getRequiredTestClass();
      verify(extensionContext).getRequiredTestMethod();
    }
  }

  /** Tests for constructor. */
  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    /** Constructs test instance. */
    ConstructorTests() {}

    /** Verifies that constructor stores all parameters. */
    @Test
    @Tag("normal")
    @DisplayName("Stores all constructor parameters")
    void storesAllParameters() {
      // Given
      final var testClass = TestContextTest.class;
      final var testMethod = mock(Method.class);
      final var configuration = mock(Configuration.class);
      final var registry = mock(DataSourceRegistry.class);

      // When
      final var context = new TestContext(testClass, testMethod, configuration, registry);

      // Then
      assertEquals(testClass, context.testClass());
      assertEquals(testMethod, context.testMethod());
      assertEquals(configuration, context.configuration());
      assertEquals(registry, context.registry());
    }
  }

  /** Tests for accessor methods. */
  @Nested
  @DisplayName("Accessor methods")
  class AccessorMethods {

    /** Constructs test instance. */
    AccessorMethods() {}

    /** Verifies that testClass() returns correct value. */
    @Test
    @Tag("normal")
    @DisplayName("testClass() returns test class")
    void testClassReturnsTestClass() {
      // Given
      final var testClass = TestContextTest.class;
      final var context =
          new TestContext(
              testClass,
              mock(Method.class),
              mock(Configuration.class),
              mock(DataSourceRegistry.class));

      // When
      final var result = context.testClass();

      // Then
      assertEquals(testClass, result);
    }

    /** Verifies that testMethod() returns correct value. */
    @Test
    @Tag("normal")
    @DisplayName("testMethod() returns test method")
    void testMethodReturnsTestMethod() {
      // Given
      final var testMethod = mock(Method.class);
      final var context =
          new TestContext(
              TestContextTest.class,
              testMethod,
              mock(Configuration.class),
              mock(DataSourceRegistry.class));

      // When
      final var result = context.testMethod();

      // Then
      assertEquals(testMethod, result);
    }

    /** Verifies that configuration() returns correct value. */
    @Test
    @Tag("normal")
    @DisplayName("configuration() returns configuration")
    void configurationReturnsConfiguration() {
      // Given
      final var configuration = mock(Configuration.class);
      final var context =
          new TestContext(
              TestContextTest.class,
              mock(Method.class),
              configuration,
              mock(DataSourceRegistry.class));

      // When
      final var result = context.configuration();

      // Then
      assertEquals(configuration, result);
    }

    /** Verifies that registry() returns correct value. */
    @Test
    @Tag("normal")
    @DisplayName("registry() returns data source registry")
    void registryReturnsRegistry() {
      // Given
      final var registry = mock(DataSourceRegistry.class);
      final var context =
          new TestContext(
              TestContextTest.class, mock(Method.class), mock(Configuration.class), registry);

      // When
      final var result = context.registry();

      // Then
      assertEquals(registry, result);
    }
  }

  /** Tests for record equality. */
  @Nested
  @DisplayName("Record equality")
  class RecordEquality {

    /** Constructs test instance. */
    RecordEquality() {}

    /** Verifies that equal instances are equal. */
    @Test
    @Tag("normal")
    @DisplayName("Equal instances are equal")
    void equalInstances_areEqual() {
      // Given
      final var testClass = TestContextTest.class;
      final var testMethod = mock(Method.class);
      final var configuration = mock(Configuration.class);
      final var registry = mock(DataSourceRegistry.class);

      final var context1 = new TestContext(testClass, testMethod, configuration, registry);
      final var context2 = new TestContext(testClass, testMethod, configuration, registry);

      // Then
      assertEquals(context1, context2);
      assertEquals(context1.hashCode(), context2.hashCode());
    }

    /** Verifies that toString() includes all fields. */
    @Test
    @Tag("normal")
    @DisplayName("toString() includes all fields")
    void toStringIncludesAllFields() {
      // Given
      final var testClass = TestContextTest.class;
      final var testMethod = mock(Method.class);
      final var configuration = mock(Configuration.class);
      final var registry = mock(DataSourceRegistry.class);

      final var context = new TestContext(testClass, testMethod, configuration, registry);

      // When
      final var result = context.toString();

      // Then
      assertNotNull(result);
      assertTrue(result.contains("TestContext"));
    }
  }

  /** Dummy test method for reflection. */
  void testMethod() {}
}
