package io.github.seijikohara.dbtester.api.extension;

import io.github.seijikohara.dbtester.api.config.Configuration;
import io.github.seijikohara.dbtester.api.config.DataSourceRegistry;
import io.github.seijikohara.dbtester.api.context.TestContext;
import io.github.seijikohara.dbtester.internal.junit.lifecycle.TestLifecycle;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Jupiter extension that coordinates dataset preparation and verification.
 *
 * <p>The extension performs three responsibilities:
 *
 * <ol>
 *   <li>Manages per-class state (configuration, {@link DataSourceRegistry}, lifecycle coordinator)
 *       using the {@link ExtensionContext} store.
 *   <li>Before each test, resolves {@link
 *       io.github.seijikohara.dbtester.api.annotation.Preparation} declarations and executes the
 *       resulting datasets.
 *   <li>After each test, resolves {@link io.github.seijikohara.dbtester.api.annotation.Expectation}
 *       declarations and validates the database contents.
 * </ol>
 */
public class DatabaseTestExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  /** Logger for tracking test execution progress and errors. */
  private static final Logger logger = LoggerFactory.getLogger(DatabaseTestExtension.class);

  /** Key for storing the DataSourceRegistry in the Store. */
  private static final String REGISTRY_KEY = "dataSourceRegistry";

  /** Key for storing the Configuration in the Store. */
  private static final String CONFIGURATION_KEY = "configuration";

  /** Key for storing the TestLifecycle in the Store. */
  private static final String LIFECYCLE_KEY = "testLifecycle";

  /**
   * Creates a database test extension with default configuration.
   *
   * <p>Configuration and {@link DataSourceRegistry} instances are created lazily per test class
   * using the JUnit {@link ExtensionContext} store mechanism. This ensures proper isolation between
   * test classes while enabling configuration sharing within a class hierarchy.
   */
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
   * @param extensionContext the extension context
   * @return the data source registry for this test class
   */
  public static DataSourceRegistry getRegistry(final ExtensionContext extensionContext) {
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    return getOrCreate(
        extensionContext, REGISTRY_KEY, DataSourceRegistry.class, DataSourceRegistry::new);
  }

  /**
   * Stores a custom configuration for the current test class.
   *
   * <p>This method allows tests to customize the framework's behavior by providing a custom {@link
   * Configuration} instance. It must be called in {@code @BeforeAll} before any test execution to
   * ensure the configuration is available when needed.
   *
   * <p>If not called, the framework uses {@link Configuration#defaults()}.
   *
   * <p><strong>Example:</strong>
   *
   * <pre>{@code
   * @BeforeAll
   * static void setup(ExtensionContext context) {
   *     Configuration config = Configuration.withConventions(
   *         new ConventionSettings(
   *             new ScenarioMarker("[TestCase]"),
   *             null,
   *             "/verify"));
   *     DatabaseTestExtension.setConfiguration(context, config);
   * }
   * }</pre>
   *
   * @param extensionContext the extension context
   * @param configuration the custom configuration to use for this test class
   * @throws NullPointerException if extensionContext or configuration is null
   */
  public static void setConfiguration(
      final ExtensionContext extensionContext, final Configuration configuration) {
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    Objects.requireNonNull(configuration, "configuration must not be null");

    final var store = getClassScopedStore(extensionContext);
    store.put(CONFIGURATION_KEY, configuration);
  }

  /**
   * Gets or creates the Configuration for the current test class.
   *
   * <p>By default, uses {@link Configuration#defaults()}. Tests can customize configuration by
   * storing a custom instance before the first test runs.
   *
   * @param extensionContext the extension context
   * @return the configuration for this test class
   */
  private static Configuration getConfiguration(final ExtensionContext extensionContext) {
    return getOrCreate(
        extensionContext, CONFIGURATION_KEY, Configuration.class, Configuration::defaults);
  }

  /**
   * Gets or creates the TestLifecycle for the current test class.
   *
   * <p>The lifecycle coordinator is created once per test class and reused across all tests.
   *
   * @param extensionContext the extension context
   * @param configuration the framework configuration
   * @return the test lifecycle coordinator
   */
  private static TestLifecycle getTestLifecycle(
      final ExtensionContext extensionContext, final Configuration configuration) {
    return getOrCreate(
        extensionContext,
        LIFECYCLE_KEY,
        TestLifecycle.class,
        () -> new TestLifecycle(configuration.loader()));
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

  /**
   * Generic get-or-create method for extension state management.
   *
   * <p>Retrieves an instance from the extension context store, creating and storing it if not
   * present. This pattern ensures lazy initialization and proper lifecycle management for per-class
   * extension state.
   *
   * @param <T> the type of the instance
   * @param extensionContext the extension context
   * @param key the storage key
   * @param type the class of the instance
   * @param factory the factory function to create new instances
   * @return the existing or newly created instance
   */
  private static <T> T getOrCreate(
      final ExtensionContext extensionContext,
      final String key,
      final Class<T> type,
      final Supplier<T> factory) {
    final var store = getClassScopedStore(extensionContext);
    return Optional.ofNullable(store.get(key, type))
        .orElseGet(
            () -> {
              final var instance = factory.get();
              store.put(key, instance);
              return instance;
            });
  }

  /**
   * Returns the class-scoped store used to hold extension state for a specific test class.
   *
   * <p>For nested test classes, this method returns the store for the top-level test class to
   * ensure that state (such as data source registrations) is shared across all nested classes.
   *
   * @param extensionContext the current extension context
   * @return store scoped to the top-level test class associated with the context
   */
  private static ExtensionContext.Store getClassScopedStore(
      final ExtensionContext extensionContext) {
    final var topLevelTestClass = getTopLevelTestClass(extensionContext);
    final var namespace = Namespace.create(DatabaseTestExtension.class, topLevelTestClass);
    return extensionContext.getRoot().getStore(namespace);
  }

  /**
   * Finds the top-level test class by traversing up the context hierarchy.
   *
   * <p>For nested test classes, this method returns the outermost test class. For non-nested
   * classes, it returns the test class itself.
   *
   * @param extensionContext the current extension context
   * @return the top-level test class
   */
  private static Class<?> getTopLevelTestClass(final ExtensionContext extensionContext) {
    return findTopLevelContext(extensionContext).getRequiredTestClass();
  }

  /**
   * Recursively finds the top-level context by traversing up the parent hierarchy.
   *
   * @param extensionContext the current extension context
   * @return the top-level extension context
   */
  private static ExtensionContext findTopLevelContext(final ExtensionContext extensionContext) {
    return extensionContext
        .getParent()
        .filter(parent -> parent.getTestClass().isPresent())
        .map(DatabaseTestExtension::findTopLevelContext)
        .orElse(extensionContext);
  }
}
