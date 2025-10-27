package io.github.seijikohara.dbtester.internal.loader;

import io.github.seijikohara.dbtester.api.annotation.DataSet;
import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
import io.github.seijikohara.dbtester.internal.domain.DataSourceName;
import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestContext;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Convention-based dataset loader that resolves CSV files using test class and method names.
 *
 * <p>Default implementation that automatically locates CSV files at {@code classpath:[test-class]/}
 * (preparation) or {@code classpath:[test-class]/expected/} (expectation). When scenario names are
 * not specified, uses the test method name as the scenario filter.
 *
 * @see DataSetLoader
 * @see io.github.seijikohara.dbtester.api.annotation.DataSet
 */
public final class TestClassNameBasedDataSetLoader implements DataSetLoader {

  /** Resolver for extracting annotation values. */
  private final AnnotationResolver annotationResolver;

  /** Factory for creating dataset instances from directories. */
  private final DataSetFactory dataSetFactory;

  /** Creates a test class name based dataset loader. */
  public TestClassNameBasedDataSetLoader() {
    this.annotationResolver = new AnnotationResolver();
    this.dataSetFactory = new DataSetFactory();
  }

  @Override
  public List<ScenarioDataSet> loadPreparationDataSets(final TestContext context) {
    final var testClass = context.testClass();
    final var testMethod = context.testMethod();
    return annotationResolver
        .findPreparation(testMethod, testClass)
        .map(
            preparation -> {
              final var dataSets = preparation.dataSets();
              // When dataSets is empty, load from convention-based location with default config
              return dataSets.length == 0
                  ? loadConventionBasedDataSet(context, null)
                  : loadDataSets(context, List.of(dataSets), null);
            })
        .orElse(List.of());
  }

  @Override
  public List<ScenarioDataSet> loadExpectationDataSets(final TestContext context) {
    final var testClass = context.testClass();
    final var testMethod = context.testMethod();
    final var expectFileSuffix = context.configuration().conventions().expectationSuffix();
    return annotationResolver
        .findExpectation(testMethod, testClass)
        .map(
            expectation -> {
              final var dataSets = expectation.dataSets();
              // When dataSets is empty, load from convention-based location with default config
              return dataSets.length == 0
                  ? loadConventionBasedDataSet(context, expectFileSuffix)
                  : loadDataSets(context, List.of(dataSets), expectFileSuffix);
            })
        .orElse(List.of());
  }

  /**
   * Loads a dataset using convention-based resolution with default configuration.
   *
   * <p>This method is called when {@code @Preparation} or {@code @Expectation} is used without
   * specifying {@code dataSets}. It creates a single dataset using:
   *
   * <ul>
   *   <li>Default resource location (convention-based: {@code classpath:[test-class]/})
   *   <li>Test method name as the scenario name
   *   <li>Default data source
   * </ul>
   *
   * @param context the test execution context
   * @param suffix the directory suffix for dataset files, or {@code null} for no suffix
   * @return list containing a single convention-based dataset
   */
  private List<ScenarioDataSet> loadConventionBasedDataSet(
      final TestContext context, final @Nullable String suffix) {
    final var testMethod = context.testMethod();
    final var testClass = context.testClass();

    final var directoryResolver = new DirectoryResolver(testClass);
    final var directory = directoryResolver.resolveDirectory(null, suffix);

    // Validate directory contains CSV files
    directoryResolver.validateDirectoryContainsCsvFiles(directory);

    // Use test method name as scenario name (will be filtered by CsvScenarioDataSet if scenario
    // marker column exists)
    // If no scenario marker column exists in CSV, all rows will be loaded regardless of scenario
    // name
    final var scenarioNames = List.of(new ScenarioName(testMethod.getName()));
    final var scenarioMarker =
        new ScenarioMarker(context.configuration().conventions().scenarioMarker());
    final var dataSet = dataSetFactory.createDataSet(directory, scenarioNames, scenarioMarker);

    // Configure default data source
    final var registry = context.registry();
    final var dataSource = registry.getDefault();
    dataSet.setDataSource(dataSource);

    return List.of(dataSet);
  }

  /**
   * Loads multiple datasets from annotations.
   *
   * @param context the test execution context
   * @param dataSetAnnotations list of dataset annotations to process
   * @param suffix the directory suffix for dataset files, or {@code null} for no suffix
   * @return collection of loaded datasets
   */
  private List<ScenarioDataSet> loadDataSets(
      final TestContext context,
      final Collection<DataSet> dataSetAnnotations,
      final @Nullable String suffix) {
    final var processor = new DataSetProcessor(context, suffix, annotationResolver, dataSetFactory);
    return dataSetAnnotations.stream().map(processor::createScenarioDataSet).toList();
  }

  /**
   * Processor for creating scenario datasets from annotations.
   *
   * <p>This inner class encapsulates the logic for processing a single DataSet annotation,
   * resolving its directory location, loading CSV files, and configuring the data source.
   */
  private static final class DataSetProcessor {
    /** The test execution context. */
    private final TestContext context;

    /** The test class being executed. */
    private final Class<?> testClass;

    /** The test method being executed. */
    private final Method testMethod;

    /** The directory suffix for dataset files, or {@code null} for no suffix. */
    private final @Nullable String suffix;

    /** Resolver for annotation values. */
    private final AnnotationResolver annotationResolver;

    /** Factory for creating datasets. */
    private final DataSetFactory dataSetFactory;

    /** Resolver for directory locations. */
    private final DirectoryResolver directoryResolver;

    /**
     * Creates a new dataset processor.
     *
     * @param context the test execution context
     * @param suffix the directory suffix, or {@code null} for no suffix
     * @param annotationResolver the annotation resolver
     * @param dataSetFactory the dataset factory
     */
    private DataSetProcessor(
        final TestContext context,
        final @Nullable String suffix,
        final AnnotationResolver annotationResolver,
        final DataSetFactory dataSetFactory) {
      this.context = context;
      this.testClass = context.testClass();
      this.testMethod = context.testMethod();
      this.suffix = suffix;
      this.annotationResolver = annotationResolver;
      this.dataSetFactory = dataSetFactory;
      this.directoryResolver = new DirectoryResolver(testClass);
    }

    /**
     * Creates a scenario dataset from a DataSet annotation.
     *
     * @param annotation the DataSet annotation
     * @return the loaded scenario dataset
     */
    private ScenarioDataSet createScenarioDataSet(final DataSet annotation) {
      final var resourceLocation = annotationResolver.extractResourceLocation(annotation);
      final var directory = resolveDirectory(resourceLocation.orElse(null));

      // Validate directory contains CSV files
      directoryResolver.validateDirectoryContainsCsvFiles(directory);

      final var scenarioNames = annotationResolver.resolveScenarioNames(annotation, testMethod);
      final var scenarioMarker =
          new ScenarioMarker(context.configuration().conventions().scenarioMarker());
      final var dataSet = dataSetFactory.createDataSet(directory, scenarioNames, scenarioMarker);

      final var dataSourceName = annotationResolver.resolveDataSourceName(annotation);
      configureDataSource(dataSet, dataSourceName.orElse(null));

      return dataSet;
    }

    /**
     * Resolves the directory location for dataset files.
     *
     * @param resourceLocation the custom resource location, or null for convention-based resolution
     * @return the resolved directory path
     */
    private Path resolveDirectory(final @Nullable String resourceLocation) {
      return directoryResolver.resolveDirectory(resourceLocation, suffix);
    }

    /**
     * Configures the data source for a dataset.
     *
     * @param dataSet the dataset to configure
     * @param dataSourceName the data source name, or null for the default data source
     */
    private void configureDataSource(
        final ScenarioDataSet dataSet, final @Nullable DataSourceName dataSourceName) {
      final var registry = context.registry();
      final var dataSource =
          Optional.ofNullable(dataSourceName)
              .map(DataSourceName::value)
              .map(registry::get)
              .orElseGet(registry::getDefault);
      dataSet.setDataSource(dataSource);
    }
  }
}
