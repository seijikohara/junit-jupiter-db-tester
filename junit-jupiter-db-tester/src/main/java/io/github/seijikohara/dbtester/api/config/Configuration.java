package io.github.seijikohara.dbtester.api.config;

import io.github.seijikohara.dbtester.api.loader.DataSetLoader;
import io.github.seijikohara.dbtester.internal.loader.TestClassNameBasedDataSetLoader;
import java.util.Objects;

/**
 * Aggregates the runtime configuration consumed by the database testing extension.
 *
 * <p>A {@code Configuration} ties together three orthogonal aspects:
 *
 * <ul>
 *   <li>{@link ConventionSettings} specify how the extension resolves dataset directories.
 *   <li>{@link OperationDefaults} provide the default database operations for preparation and
 *       expectation phases.
 *   <li>{@link DataSetLoader} describes how datasets are materialised and filtered.
 * </ul>
 *
 * <p>The record is immutable; once created it can safely be shared across threads and reused for
 * the entire lifecycle of a test class.
 *
 * @param conventions resolution rules for locating datasets
 * @param operations default database operations
 * @param loader strategy for constructing datasets
 */
public record Configuration(
    ConventionSettings conventions, OperationDefaults operations, DataSetLoader loader) {

  /** Default dataset loader instance used by factory methods. */
  private static final DataSetLoader DEFAULT_LOADER = new TestClassNameBasedDataSetLoader();

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
   * Returns a configuration that applies the framework defaults for all components.
   *
   * @return configuration initialised with standard conventions, operations, and loader
   */
  public static Configuration defaults() {
    return new Configuration(
        ConventionSettings.standard(), OperationDefaults.standard(), DEFAULT_LOADER);
  }

  /**
   * Creates a configuration that overrides the convention settings while keeping other components
   * on their defaults.
   *
   * @param conventions convention settings to apply
   * @return configuration composed of the supplied conventions and default operations/loader
   */
  public static Configuration withConventions(final ConventionSettings conventions) {
    Objects.requireNonNull(conventions, "conventions must not be null");
    return new Configuration(conventions, OperationDefaults.standard(), DEFAULT_LOADER);
  }

  /**
   * Creates a configuration that overrides the default operations while leaving other components on
   * their conventional values.
   *
   * @param operations operation defaults to apply
   * @return configuration composed of standard conventions, the supplied operations, and default
   *     loader
   */
  public static Configuration withOperations(final OperationDefaults operations) {
    Objects.requireNonNull(operations, "operations must not be null");
    return new Configuration(ConventionSettings.standard(), operations, DEFAULT_LOADER);
  }

  /**
   * Creates a configuration that uses a custom dataset loader and default values for the remaining
   * components.
   *
   * @param loader custom dataset loader implementation
   * @return configuration constructed with standard conventions, standard operations, and the
   *     supplied loader
   */
  public static Configuration withLoader(final DataSetLoader loader) {
    Objects.requireNonNull(loader, "loader must not be null");
    return new Configuration(ConventionSettings.standard(), OperationDefaults.standard(), loader);
  }
}
