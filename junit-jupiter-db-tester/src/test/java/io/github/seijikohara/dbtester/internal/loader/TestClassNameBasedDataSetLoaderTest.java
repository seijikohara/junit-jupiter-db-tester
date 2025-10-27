package io.github.seijikohara.dbtester.internal.loader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.api.annotation.DataSet;
import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import io.github.seijikohara.dbtester.config.Configuration;
import io.github.seijikohara.dbtester.config.DataSourceRegistry;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestContext;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link TestClassNameBasedDataSetLoader}. */
@DisplayName("TestClassNameBasedDataSetLoader")
class TestClassNameBasedDataSetLoaderTest {

  /** Dataset loader under test. */
  private TestClassNameBasedDataSetLoader loader;

  /** Constructs test instance. */
  TestClassNameBasedDataSetLoaderTest() {}

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    loader = new TestClassNameBasedDataSetLoader();
  }

  /** Test class with Preparation annotation. */
  @Preparation
  static class TestClassWithPreparation {
    /** Creates test instance. */
    TestClassWithPreparation() {}

    /** Test method. */
    public void testMethod() {}
  }

  /** Test class with Expectation annotation. */
  @Expectation
  static class TestClassWithExpectation {
    /** Creates test instance. */
    TestClassWithExpectation() {}

    /** Test method. */
    public void testMethod() {}
  }

  /** Test class without annotations. */
  static class TestClassWithoutAnnotations {
    /** Creates test instance. */
    TestClassWithoutAnnotations() {}

    /** Test method. */
    public void testMethod() {}
  }

  /** Test class with custom dataset configuration. */
  static class TestClassWithCustomDataSet {
    /** Creates test instance. */
    TestClassWithCustomDataSet() {}

    /** Test method with custom DataSet annotation. */
    @Preparation(dataSets = {@DataSet(scenarioNames = {"scenario1"})})
    public void testMethodWithCustomDataSet() {}
  }

  /** Real test class with actual classpath resources. */
  @Preparation
  @Expectation
  static class RealTestClass {
    /** Creates test instance. */
    RealTestClass() {}

    /** Test method. */
    public void testMethod() {}
  }

  /** Tests for loadPreparationDataSets method. */
  @Nested
  @DisplayName("loadPreparationDataSets() method")
  class LoadPreparationDataSetsMethod {

    /** Constructs test instance. */
    LoadPreparationDataSetsMethod() {}

    /**
     * Verifies that empty list is returned when no annotation exists.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty list when no Preparation annotation")
    void returnsEmptyList_whenNoAnnotation() throws NoSuchMethodException {
      // Given
      final var testMethod = TestClassWithoutAnnotations.class.getMethod("testMethod");
      final var testClass = TestClassWithoutAnnotations.class;
      final var context = createContext(testMethod, testClass);

      // When
      final var result = loader.loadPreparationDataSets(context);

      // Then
      assertTrue(result.isEmpty());
    }

    /**
     * Verifies that dataset is loaded from classpath resources.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Loads dataset from classpath resources")
    void loadsDataSet_fromClasspathResources() throws NoSuchMethodException {
      // Given
      final var testMethod = RealTestClass.class.getMethod("testMethod");
      final var testClass = RealTestClass.class;
      final var context = createContext(testMethod, testClass);

      // When
      final var result = loader.loadPreparationDataSets(context);

      // Then
      assertEquals(1, result.size());
      final var dataSet = result.get(0);
      assertNotNull(dataSet);
      assertEquals(1, dataSet.getTables().size());
      assertTrue(dataSet.getDataSource().isPresent());
    }
  }

  /** Tests for loadExpectationDataSets method. */
  @Nested
  @DisplayName("loadExpectationDataSets() method")
  class LoadExpectationDataSetsMethod {

    /** Constructs test instance. */
    LoadExpectationDataSetsMethod() {}

    /**
     * Verifies that empty list is returned when no annotation exists.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty list when no Expectation annotation")
    void returnsEmptyList_whenNoAnnotation() throws NoSuchMethodException {
      // Given
      final var testMethod = TestClassWithoutAnnotations.class.getMethod("testMethod");
      final var testClass = TestClassWithoutAnnotations.class;
      final var context = createContext(testMethod, testClass);

      // When
      final var result = loader.loadExpectationDataSets(context);

      // Then
      assertTrue(result.isEmpty());
    }

    /**
     * Verifies that expectation dataset is loaded from classpath resources.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Loads expectation dataset from classpath resources")
    void loadsExpectationDataSet_fromClasspathResources() throws NoSuchMethodException {
      // Given
      final var testMethod = RealTestClass.class.getMethod("testMethod");
      final var testClass = RealTestClass.class;
      final var context = createContext(testMethod, testClass);

      // When
      final var result = loader.loadExpectationDataSets(context);

      // Then
      assertEquals(1, result.size());
      final var dataSet = result.get(0);
      assertNotNull(dataSet);
      assertEquals(1, dataSet.getTables().size());
      assertTrue(dataSet.getDataSource().isPresent());
    }
  }

  /**
   * Creates a test context.
   *
   * @param testMethod the test method
   * @param testClass the test class
   * @return the test context
   */
  private TestContext createContext(final Method testMethod, final Class<?> testClass) {
    final var configuration = Configuration.defaults();
    final var dataSource = mock(DataSource.class);
    final var registry = mock(DataSourceRegistry.class);
    when(registry.getDefault()).thenReturn(dataSource);

    return new TestContext(testClass, testMethod, configuration, registry);
  }
}
