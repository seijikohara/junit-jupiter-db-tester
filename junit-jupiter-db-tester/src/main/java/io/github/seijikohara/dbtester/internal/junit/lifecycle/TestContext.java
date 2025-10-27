package io.github.seijikohara.dbtester.internal.junit.lifecycle;

import io.github.seijikohara.dbtester.config.Configuration;
import io.github.seijikohara.dbtester.config.DataSourceRegistry;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Immutable test execution context.
 *
 * <p>This record captures all the contextual information needed for a single test execution,
 * including the test class, test method, framework configuration, and data source registry. It
 * provides a type-safe, immutable snapshot of the test environment.
 *
 * <h2>Design Rationale</h2>
 *
 * <p>By consolidating all context information in one immutable record, we achieve:
 *
 * <ul>
 *   <li>Thread safety - no mutable state to synchronize
 *   <li>Testability - easy to create test contexts for unit testing
 *   <li>Clarity - all required information is explicit
 *   <li>Safety - cannot accidentally modify context during test execution
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * // Create from JUnit ExtensionContext
 * TestContext context = TestContext.from(
 *     junitContext,
 *     configuration,
 *     registry
 * );
 *
 * // Access context information
 * Class<?> testClass = context.testClass();
 * Method testMethod = context.testMethod();
 * Configuration config = context.configuration();
 * DataSourceRegistry registry = context.registry();
 * }</pre>
 *
 * <p>This record is intended for internal framework use.
 *
 * @param testClass the test class being executed
 * @param testMethod the test method being executed
 * @param configuration the framework configuration
 * @param registry the data source registry
 * @see TestLifecycle
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 */
public record TestContext(
    Class<?> testClass,
    Method testMethod,
    Configuration configuration,
    DataSourceRegistry registry) {

  /**
   * Creates a test context from a JUnit extension context.
   *
   * @param extensionContext the JUnit extension context (must not be null)
   * @param configuration the framework configuration (must not be null)
   * @param registry the data source registry (must not be null)
   * @return a new test context
   */
  public static TestContext from(
      final ExtensionContext extensionContext,
      final Configuration configuration,
      final DataSourceRegistry registry) {
    final var testClass = extensionContext.getRequiredTestClass();
    final var testMethod = extensionContext.getRequiredTestMethod();
    return new TestContext(testClass, testMethod, configuration, registry);
  }
}
