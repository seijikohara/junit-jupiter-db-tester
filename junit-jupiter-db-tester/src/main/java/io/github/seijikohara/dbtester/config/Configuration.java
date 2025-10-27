package io.github.seijikohara.dbtester.config;

import io.github.seijikohara.dbtester.internal.loader.DataSetLoader;
import io.github.seijikohara.dbtester.internal.loader.TestClassNameBasedDataSetLoader;
import java.util.Objects;

/**
 * Immutable configuration for the database testing framework.
 *
 * <p>This record aggregates all configuration settings needed for database test execution,
 * including conventions for locating dataset files, default database operations, and the dataset
 * loader implementation. The immutable design ensures thread safety and consistent behavior across
 * concurrent tests.
 *
 * <h2>Configuration Components</h2>
 *
 * <ul>
 *   <li><strong>{@link ConventionSettings}:</strong> Defines conventions for locating dataset files
 *       relative to test classes
 *   <li><strong>{@link OperationDefaults}:</strong> Specifies default database operations for
 *       preparation and expectation phases
 *   <li><strong>{@link DataSetLoader}:</strong> Provides the strategy for loading test data files
 * </ul>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This record is immutable, ensuring thread-safe access across concurrent tests. Once created,
 * configuration values cannot be modified, preventing configuration drift during test execution.
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Use default configuration
 * Configuration config = Configuration.defaults();
 *
 * // Customize conventions only
 * ConventionSettings custom = new ConventionSettings("test-data", "/expected", "[Scenario]");
 * Configuration customConfig = Configuration.withConventions(custom);
 *
 * // Customize operations only
 * OperationDefaults ops = new OperationDefaults(Operation.INSERT, Operation.NONE);
 * Configuration customOps = Configuration.withOperations(ops);
 *
 * // Full customization
 * Configuration fullyCustom = new Configuration(
 *     custom,
 *     ops,
 *     new TestClassNameBasedDataSetLoader()
 * );
 * }</pre>
 *
 * @param conventions conventions for dataset file resolution
 * @param operations default database operations for test phases
 * @param loader dataset loader implementation
 * @see ConventionSettings
 * @see OperationDefaults
 * @see DataSourceRegistry
 */
public record Configuration(
    ConventionSettings conventions, OperationDefaults operations, DataSetLoader loader) {

  /**
   * Compact constructor that validates all record components.
   *
   * @param conventions conventions for dataset file resolution
   * @param operations default database operations for test phases
   * @param loader dataset loader implementation
   * @throws NullPointerException if any parameter is {@code null}
   */
  public Configuration {
    Objects.requireNonNull(conventions, "conventions must not be null");
    Objects.requireNonNull(operations, "operations must not be null");
    Objects.requireNonNull(loader, "loader must not be null");
  }

  /**
   * Creates a configuration with standard default values.
   *
   * <p>Uses standard conventions, operation defaults, and the default test class-based dataset
   * loader. This is suitable for most testing scenarios.
   *
   * @return a configuration with standard defaults
   */
  public static Configuration defaults() {
    return new Configuration(
        ConventionSettings.standard(),
        OperationDefaults.standard(),
        new TestClassNameBasedDataSetLoader());
  }

  /**
   * Creates a configuration with custom conventions and standard values for other components.
   *
   * @param conventions the custom convention settings
   * @return a configuration using the specified conventions
   * @throws NullPointerException if {@code conventions} is {@code null}
   */
  public static Configuration withConventions(final ConventionSettings conventions) {
    Objects.requireNonNull(conventions, "conventions must not be null");
    return new Configuration(
        conventions, OperationDefaults.standard(), new TestClassNameBasedDataSetLoader());
  }

  /**
   * Creates a configuration with custom operation defaults and standard values for other
   * components.
   *
   * @param operations the custom operation defaults
   * @return a configuration using the specified operation defaults
   * @throws NullPointerException if {@code operations} is {@code null}
   */
  public static Configuration withOperations(final OperationDefaults operations) {
    Objects.requireNonNull(operations, "operations must not be null");
    return new Configuration(
        ConventionSettings.standard(), operations, new TestClassNameBasedDataSetLoader());
  }

  /**
   * Creates a configuration with a custom dataset loader and standard values for other components.
   *
   * @param loader the custom dataset loader
   * @return a configuration using the specified loader
   * @throws NullPointerException if {@code loader} is {@code null}
   */
  public static Configuration withLoader(final DataSetLoader loader) {
    Objects.requireNonNull(loader, "loader must not be null");
    return new Configuration(ConventionSettings.standard(), OperationDefaults.standard(), loader);
  }
}
