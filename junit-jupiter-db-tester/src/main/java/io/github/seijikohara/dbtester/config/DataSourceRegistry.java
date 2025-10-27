package io.github.seijikohara.dbtester.config;

import io.github.seijikohara.dbtester.exception.DataSourceNotFoundException;
import io.github.seijikohara.dbtester.internal.domain.DataSourceName;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;

/**
 * Thread-safe registry for managing database data sources.
 *
 * <p>This class maintains a central registry of named data sources and a default data source for
 * use during test execution. Unlike most framework classes, this registry is mutable to support
 * runtime registration in test setup methods such as {@code @BeforeAll} or {@code @BeforeEach}.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>All operations are thread-safe for concurrent test execution. Named data sources are stored in
 * a {@link ConcurrentHashMap}, and the default data source uses volatile semantics for safe
 * publication across threads.
 *
 * <h2>Design Rationale</h2>
 *
 * <p>This registry is intentionally mutable because data sources must be registered dynamically
 * after framework initialization. Test classes register their specific database connections during
 * setup, and the registry serves as a shared, globally accessible resource throughout test
 * execution.
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * DataSourceRegistry registry = new DataSourceRegistry();
 *
 * // Register the default data source
 * registry.registerDefault(dataSource);
 *
 * // Register additional named data sources
 * registry.register("warehouse", warehouseDataSource);
 * registry.register("analytics", analyticsDataSource);
 *
 * // Retrieve data sources
 * DataSource defaultDs = registry.getDefault();
 * DataSource warehouseDs = registry.get("warehouse");
 * }</pre>
 *
 * @see Configuration
 */
public final class DataSourceRegistry {

  /** Internal name used for the default data source in the named map. */
  private static final DataSourceName DEFAULT_DATA_SOURCE_NAME =
      new DataSourceName("_defaultDataSource_");

  /** Thread-safe map of named data sources. */
  private final ConcurrentMap<DataSourceName, DataSource> dataSources = new ConcurrentHashMap<>();

  /** The default data source (volatile for thread-safe visibility). */
  private volatile @Nullable DataSource defaultDataSource;

  /** Creates an empty data source registry. */
  public DataSourceRegistry() {}

  /**
   * Registers the default data source.
   *
   * <p>This method is a convenience for single-database test scenarios. The specified data source
   * becomes the default and is also registered internally for framework use.
   *
   * @param dataSource the data source to register as the default
   * @throws NullPointerException if {@code dataSource} is {@code null}
   */
  public void registerDefault(final DataSource dataSource) {
    Objects.requireNonNull(dataSource, "dataSource must not be null");

    this.defaultDataSource = dataSource;
    dataSources.put(DEFAULT_DATA_SOURCE_NAME, dataSource);
  }

  /**
   * Registers a data source with the specified name.
   *
   * <p>If the name is empty or matches the internal default marker, the data source is registered
   * as the default instead of as a named data source.
   *
   * @param name the data source name
   * @param dataSource the data source to register
   * @throws NullPointerException if either parameter is {@code null}
   */
  public void register(final String name, final DataSource dataSource) {
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(dataSource, "dataSource must not be null");

    if (name.trim().isEmpty() || DEFAULT_DATA_SOURCE_NAME.value().equals(name)) {
      registerDefault(dataSource);
    } else {
      dataSources.put(new DataSourceName(name), dataSource);
    }
  }

  /**
   * Retrieves the default data source.
   *
   * @return the default data source
   * @throws DataSourceNotFoundException if no default data source is registered
   */
  public DataSource getDefault() {
    return Optional.ofNullable(defaultDataSource)
        .orElseThrow(() -> new DataSourceNotFoundException("No default data source registered"));
  }

  /**
   * Retrieves a data source by name, falling back to the default if the name is empty.
   *
   * @param name the data source name, or empty string for default
   * @return the data source
   * @throws DataSourceNotFoundException if the named data source is not found and no default is
   *     registered
   */
  public DataSource get(final @Nullable String name) {
    final var namedDataSource =
        Optional.ofNullable(name)
            .filter(dataSourceName -> !dataSourceName.isEmpty())
            .map(DataSourceName::new)
            .flatMap(dataSourceName -> Optional.ofNullable(dataSources.get(dataSourceName)));

    return namedDataSource
        .or(() -> Optional.ofNullable(defaultDataSource))
        .orElseThrow(() -> new DataSourceNotFoundException(buildMissingDataSourceMessage(name)));
  }

  /**
   * Retrieves a data source by name as an Optional.
   *
   * @param name the data source name (must not be null)
   * @return an Optional containing the data source, or empty if not found
   */
  public Optional<DataSource> find(final String name) {
    Objects.requireNonNull(name, "name must not be null");

    return Optional.ofNullable(dataSources.get(new DataSourceName(name)));
  }

  /**
   * Checks if a default data source is registered.
   *
   * @return true if a default data source exists
   */
  public boolean hasDefault() {
    return defaultDataSource != null;
  }

  /**
   * Checks if a named data source is registered.
   *
   * @param name the data source name (must not be null)
   * @return true if the named data source exists
   */
  public boolean has(final String name) {
    Objects.requireNonNull(name, "name must not be null");

    return dataSources.containsKey(new DataSourceName(name));
  }

  /**
   * Removes all registered data sources including the default.
   *
   * <p>This method is useful for test cleanup or resetting the registry state.
   */
  public void clear() {
    dataSources.clear();
    defaultDataSource = null;
  }

  /**
   * Builds an error message for missing data source scenarios.
   *
   * @param name the data source name that was not found
   * @return formatted error message
   */
  private String buildMissingDataSourceMessage(final @Nullable String name) {
    return Optional.ofNullable(name)
        .filter(dataSourceName -> !dataSourceName.isEmpty())
        .map(
            dataSourceName ->
                String.format("No data source registered for name: %s", dataSourceName))
        .orElse("No default data source registered");
  }
}
