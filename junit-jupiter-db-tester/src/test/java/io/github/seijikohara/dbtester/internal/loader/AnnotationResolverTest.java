package io.github.seijikohara.dbtester.internal.loader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.api.annotation.DataSet;
import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import io.github.seijikohara.dbtester.internal.domain.DataSourceName;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link AnnotationResolver}. */
@DisplayName("AnnotationResolver")
class AnnotationResolverTest {

  /** Annotation resolver under test. */
  private AnnotationResolver resolver;

  /** Constructs test instance. */
  AnnotationResolverTest() {}

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    resolver = new AnnotationResolver();
  }

  // Test classes with annotations for testing inheritance

  /** Base test class with class-level annotations. */
  @Preparation
  @Expectation
  static class BaseTestClass {
    /** Creates test instance. */
    BaseTestClass() {}

    /** Test method without annotations. */
    public void methodWithoutAnnotation() {}
  }

  /** Child test class inheriting from base. */
  static class ChildTestClass extends BaseTestClass {
    /** Creates test instance. */
    ChildTestClass() {}

    /** Test method with method-level annotations. */
    @Preparation
    @Expectation
    public void methodWithAnnotation() {}

    /** Test method without annotations (should inherit from class). */
    @Override
    public void methodWithoutAnnotation() {}
  }

  /** Test class without annotations. */
  static class ClassWithoutAnnotations {
    /** Creates test instance. */
    ClassWithoutAnnotations() {}

    /** Test method without annotations. */
    public void testMethod() {}
  }

  /** Tests for findPreparation method. */
  @Nested
  @DisplayName("findPreparation() method")
  class FindPreparationMethod {

    /** Constructs test instance. */
    FindPreparationMethod() {}

    /**
     * Verifies that method-level annotation is found.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Finds method-level Preparation annotation")
    void findsMethodLevelAnnotation() throws NoSuchMethodException {
      // Given
      final var method = ChildTestClass.class.getMethod("methodWithAnnotation");
      final var testClass = ChildTestClass.class;

      // When
      final var result = resolver.findPreparation(method, testClass);

      // Then
      assertTrue(result.isPresent());
    }

    /**
     * Verifies that class-level annotation is found.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Finds class-level Preparation annotation")
    void findsClassLevelAnnotation() throws NoSuchMethodException {
      // Given
      final var method = BaseTestClass.class.getMethod("methodWithoutAnnotation");
      final var testClass = BaseTestClass.class;

      // When
      final var result = resolver.findPreparation(method, testClass);

      // Then
      assertTrue(result.isPresent());
    }

    /**
     * Verifies that parent class annotation is found.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Finds parent class Preparation annotation")
    void findsParentClassAnnotation() throws NoSuchMethodException {
      // Given
      final var method = ChildTestClass.class.getMethod("methodWithoutAnnotation");
      final var testClass = ChildTestClass.class;

      // When
      final var result = resolver.findPreparation(method, testClass);

      // Then
      assertTrue(result.isPresent());
    }

    /**
     * Verifies that empty is returned when no annotation exists.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty when no Preparation annotation exists")
    void returnsEmpty_whenNoAnnotationExists() throws NoSuchMethodException {
      // Given
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");
      final var testClass = ClassWithoutAnnotations.class;

      // When
      final var result = resolver.findPreparation(method, testClass);

      // Then
      assertTrue(result.isEmpty());
    }
  }

  /** Tests for findExpectation method. */
  @Nested
  @DisplayName("findExpectation() method")
  class FindExpectationMethod {

    /** Constructs test instance. */
    FindExpectationMethod() {}

    /**
     * Verifies that method-level annotation is found.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Finds method-level Expectation annotation")
    void findsMethodLevelAnnotation() throws NoSuchMethodException {
      // Given
      final var method = ChildTestClass.class.getMethod("methodWithAnnotation");
      final var testClass = ChildTestClass.class;

      // When
      final var result = resolver.findExpectation(method, testClass);

      // Then
      assertTrue(result.isPresent());
    }

    /**
     * Verifies that class-level annotation is found.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Finds class-level Expectation annotation")
    void findsClassLevelAnnotation() throws NoSuchMethodException {
      // Given
      final var method = BaseTestClass.class.getMethod("methodWithoutAnnotation");
      final var testClass = BaseTestClass.class;

      // When
      final var result = resolver.findExpectation(method, testClass);

      // Then
      assertTrue(result.isPresent());
    }

    /**
     * Verifies that empty is returned when no annotation exists.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty when no Expectation annotation exists")
    void returnsEmpty_whenNoAnnotationExists() throws NoSuchMethodException {
      // Given
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");
      final var testClass = ClassWithoutAnnotations.class;

      // When
      final var result = resolver.findExpectation(method, testClass);

      // Then
      assertTrue(result.isEmpty());
    }
  }

  /** Tests for resolveScenarioNames method. */
  @Nested
  @DisplayName("resolveScenarioNames() method")
  class ResolveScenarioNamesMethod {

    /** Constructs test instance. */
    ResolveScenarioNamesMethod() {}

    /**
     * Verifies that scenario names are resolved from annotation.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Resolves scenario names from annotation")
    void resolvesScenarioNamesFromAnnotation() throws NoSuchMethodException {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.scenarioNames()).thenReturn(new String[] {"scenario1", "scenario2"});
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");

      // When
      final var result = resolver.resolveScenarioNames(annotation, method);

      // Then
      assertEquals(2, result.size());
      assertEquals(new ScenarioName("scenario1"), result.get(0));
      assertEquals(new ScenarioName("scenario2"), result.get(1));
    }

    /**
     * Verifies that whitespace is trimmed from scenario names.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Trims whitespace from scenario names")
    void trimsWhitespace() throws NoSuchMethodException {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.scenarioNames()).thenReturn(new String[] {"  scenario1  ", " scenario2 "});
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");

      // When
      final var result = resolver.resolveScenarioNames(annotation, method);

      // Then
      assertEquals(2, result.size());
      assertEquals(new ScenarioName("scenario1"), result.get(0));
      assertEquals(new ScenarioName("scenario2"), result.get(1));
    }

    /**
     * Verifies that empty strings are filtered out.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Filters out empty scenario names")
    void filtersEmptyNames() throws NoSuchMethodException {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.scenarioNames())
          .thenReturn(new String[] {"scenario1", "", "  ", "scenario2"});
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");

      // When
      final var result = resolver.resolveScenarioNames(annotation, method);

      // Then
      assertEquals(2, result.size());
      assertEquals(new ScenarioName("scenario1"), result.get(0));
      assertEquals(new ScenarioName("scenario2"), result.get(1));
    }

    /**
     * Verifies that method name is used when no scenarios specified.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Uses method name when no scenarios specified")
    void usesMethodName_whenNoScenariosSpecified() throws NoSuchMethodException {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.scenarioNames()).thenReturn(new String[] {});
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");

      // When
      final var result = resolver.resolveScenarioNames(annotation, method);

      // Then
      assertEquals(1, result.size());
      assertEquals(new ScenarioName("testMethod"), result.get(0));
    }

    /**
     * Verifies that method name is used when only empty scenarios.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Uses method name when only empty scenarios")
    void usesMethodName_whenOnlyEmptyScenarios() throws NoSuchMethodException {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.scenarioNames()).thenReturn(new String[] {"", "  "});
      final var method = ClassWithoutAnnotations.class.getMethod("testMethod");

      // When
      final var result = resolver.resolveScenarioNames(annotation, method);

      // Then
      assertEquals(1, result.size());
      assertEquals(new ScenarioName("testMethod"), result.get(0));
    }
  }

  /** Tests for extractResourceLocation method. */
  @Nested
  @DisplayName("extractResourceLocation() method")
  class ExtractResourceLocationMethod {

    /** Constructs test instance. */
    ExtractResourceLocationMethod() {}

    /** Verifies that resource location is extracted. */
    @Test
    @Tag("normal")
    @DisplayName("Extracts resource location from annotation")
    void extractsResourceLocation() {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.resourceLocation()).thenReturn("custom/location");

      // When
      final var result = resolver.extractResourceLocation(annotation);

      // Then
      assertTrue(result.isPresent());
      if (result.isPresent()) {
        assertEquals("custom/location", result.get());
      }
    }

    /** Verifies that empty is returned when location is empty. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns empty when resource location is empty")
    void returnsEmpty_whenLocationIsEmpty() {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.resourceLocation()).thenReturn("");

      // When
      final var result = resolver.extractResourceLocation(annotation);

      // Then
      assertTrue(result.isEmpty());
    }
  }

  /** Tests for resolveDataSourceName method. */
  @Nested
  @DisplayName("resolveDataSourceName() method")
  class ResolveDataSourceNameMethod {

    /** Constructs test instance. */
    ResolveDataSourceNameMethod() {}

    /** Verifies that data source name is resolved. */
    @Test
    @Tag("normal")
    @DisplayName("Resolves data source name from annotation")
    void resolvesDataSourceName() {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.dataSourceName()).thenReturn("customDataSource");

      // When
      final var result = resolver.resolveDataSourceName(annotation);

      // Then
      assertTrue(result.isPresent());
      if (result.isPresent()) {
        assertEquals(new DataSourceName("customDataSource"), result.get());
      }
    }

    /** Verifies that empty is returned when data source name is empty. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns empty when data source name is empty")
    void returnsEmpty_whenDataSourceNameIsEmpty() {
      // Given
      final var annotation = mock(DataSet.class);
      when(annotation.dataSourceName()).thenReturn("");

      // When
      final var result = resolver.resolveDataSourceName(annotation);

      // Then
      assertTrue(result.isEmpty());
    }
  }
}
