package io.github.seijikohara.dbtester.extension;

import io.github.seijikohara.dbtester.config.Configuration;
import io.github.seijikohara.dbtester.config.DataSourceRegistry;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestContext;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestLifecycle;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Jupiter extension that automates database test data preparation and validation.
 *
 * <p>This extension integrates the database testing framework with JUnit Jupiter's extension model.
 * It processes {@link io.github.seijikohara.dbtester.api.annotation.Preparation @Preparation}
 * annotations before each test to load CSV datasets into the database, and {@link
 * io.github.seijikohara.dbtester.api.annotation.Expectation @Expectation} annotations after each
 * test to validate the resulting database state. The extension is thread-safe and supports
 * concurrent test execution.
 *
 * <h2>Usage</h2>
 *
 * <p>Register this extension using {@code @ExtendWith} at the class level:
 *
 * <pre>{@code
 * @ExtendWith(DatabaseTestExtension.class)
 * class ProductDatabaseTest {
 *
 *     @BeforeAll
 *     static void setup(ExtensionContext context) {
 *         DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
 *         registry.registerDefault(createDataSource());
 *     }
 *
 *     @Test
 *     @Preparation
 *     @Expectation
 *     void shouldInsertProduct() {
 *         // Test implementation
 *     }
 * }
 * }</pre>
 *
 * <h2>Data Source Management</h2>
 *
 * <p>The extension automatically creates and manages a {@link DataSourceRegistry} using JUnit's
 * {@link ExtensionContext.Store}. Test classes must register their data sources in a
 * {@code @BeforeAll} method by calling {@link #getRegistry(ExtensionContext)}.
 *
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 * @see Configuration
 * @see DataSourceRegistry
 */
public class DatabaseTestExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  /** Logger for tracking test execution progress and errors. */
  private static final Logger logger = LoggerFactory.getLogger(DatabaseTestExtension.class);

  /**
   * Namespace for storing extension state in JUnit's Store.
   *
   * <p>Uses a unique namespace per extension to avoid conflicts with other extensions.
   */
  private static final Namespace NAMESPACE = Namespace.create(DatabaseTestExtension.class);

  /** Key for storing the DataSourceRegistry in the Store. */
  private static final String REGISTRY_KEY = "dataSourceRegistry";

  /** Key for storing the Configuration in the Store. */
  private static final String CONFIGURATION_KEY = "configuration";

  /** Key for storing the TestLifecycle in the Store. */
  private static final String LIFECYCLE_KEY = "testLifecycle";

  /** Creates a database test extension with default configuration. */
  public DatabaseTestExtension() {
    // Configuration and registry are created lazily per test class via Store
  }

  /**
   * Gets or creates the DataSourceRegistry for the current test class.
   *
   * <p>The registry is stored in the root context's Store, making it available to all tests in the
   * class hierarchy. Tests should call this method in {@code @BeforeAll} to register data sources.
   *
   * <p><strong>Example:</strong>
   *
   * <pre>{@code
   * @BeforeAll
   * static void setup(ExtensionContext context) {
   *     DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
   *     registry.registerDefault(dataSource);
   * }
   * }</pre>
   *
   * @param context the extension context
   * @return the data source registry for this test class
   */
  public static DataSourceRegistry getRegistry(final ExtensionContext context) {
    Objects.requireNonNull(context, "context must not be null");
    final var store = context.getRoot().getStore(NAMESPACE);
    return Optional.ofNullable(store.get(REGISTRY_KEY, DataSourceRegistry.class))
        .orElseGet(
            () -> {
              final var newRegistry = new DataSourceRegistry();
              store.put(REGISTRY_KEY, newRegistry);
              return newRegistry;
            });
  }

  /**
   * Gets or creates the Configuration for the current test class.
   *
   * <p>By default, uses {@link Configuration#defaults()}. Tests can customize configuration by
   * storing a custom instance before the first test runs.
   *
   * @param context the extension context
   * @return the configuration for this test class
   */
  private static Configuration getConfiguration(final ExtensionContext context) {
    final var store = context.getRoot().getStore(NAMESPACE);
    return Optional.ofNullable(store.get(CONFIGURATION_KEY, Configuration.class))
        .orElseGet(
            () -> {
              final var newConfig = Configuration.defaults();
              store.put(CONFIGURATION_KEY, newConfig);
              return newConfig;
            });
  }

  /**
   * Gets or creates the TestLifecycle for the current test class.
   *
   * <p>The lifecycle coordinator is created once per test class and reused across all tests.
   *
   * @param context the extension context
   * @param configuration the framework configuration
   * @return the test lifecycle coordinator
   */
  private static TestLifecycle getTestLifecycle(
      final ExtensionContext context, final Configuration configuration) {
    final var store = context.getRoot().getStore(NAMESPACE);
    return Optional.ofNullable(store.get(LIFECYCLE_KEY, TestLifecycle.class))
        .orElseGet(
            () -> {
              final var newLifecycle = new TestLifecycle(configuration.loader());
              store.put(LIFECYCLE_KEY, newLifecycle);
              return newLifecycle;
            });
  }

  /**
   * Executes before each test method to prepare database state.
   *
   * <p>Loads and applies preparation datasets specified by {@link
   * io.github.seijikohara.dbtester.api.annotation.Preparation @Preparation} annotations.
   *
   * @param extensionContext the JUnit extension context
   */
  @Override
  public void beforeEach(final ExtensionContext extensionContext) {
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    final var configuration = getConfiguration(extensionContext);
    final var dataSourceRegistry = getRegistry(extensionContext);
    final var testLifecycle = getTestLifecycle(extensionContext, configuration);

    final var testContext = TestContext.from(extensionContext, configuration, dataSourceRegistry);
    logger.debug(
        "Executing beforeEach for {}.{}()",
        testContext.testClass().getSimpleName(),
        testContext.testMethod().getName());

    testLifecycle.executePreparation(testContext);
  }

  /**
   * Executes after each test method to validate database state.
   *
   * <p>Loads and verifies expectation datasets specified by {@link
   * io.github.seijikohara.dbtester.api.annotation.Expectation @Expectation} annotations.
   *
   * @param extensionContext the JUnit extension context
   */
  @Override
  public void afterEach(final ExtensionContext extensionContext) {
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    final var configuration = getConfiguration(extensionContext);
    final var dataSourceRegistry = getRegistry(extensionContext);
    final var testLifecycle = getTestLifecycle(extensionContext, configuration);

    final var testContext = TestContext.from(extensionContext, configuration, dataSourceRegistry);
    logger.debug(
        "Executing afterEach for {}.{}()",
        testContext.testClass().getSimpleName(),
        testContext.testMethod().getName());

    testLifecycle.executeVerification(testContext);
  }

  /**
   * Determines if this extension can resolve the specified parameter.
   *
   * <p>This extension supports injecting {@link ExtensionContext} into test methods and lifecycle
   * methods (including {@code @BeforeAll} when using {@code @TestInstance(Lifecycle.PER_CLASS)}).
   *
   * @param parameterContext the context for the parameter
   * @param extensionContext the extension context
   * @return {@code true} if the parameter type is {@link ExtensionContext}
   */
  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext) {
    Objects.requireNonNull(parameterContext, "parameterContext must not be null");
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    return parameterContext.getParameter().getType() == ExtensionContext.class;
  }

  /**
   * Resolves the parameter value.
   *
   * <p>Returns the {@link ExtensionContext} instance for the current test execution.
   *
   * @param parameterContext the context for the parameter
   * @param extensionContext the extension context
   * @return the extension context instance
   */
  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext) {
    Objects.requireNonNull(parameterContext, "parameterContext must not be null");
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    return extensionContext;
  }
}
