package io.github.seijikohara.dbtester.api.context;

import io.github.seijikohara.dbtester.api.config.Configuration;
import io.github.seijikohara.dbtester.api.config.DataSourceRegistry;
import java.lang.reflect.Method;
import java.util.Objects;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Immutable snapshot of the information required to execute a single test phase.
 *
 * @param testClass class that owns the running test
 * @param testMethod concrete test method
 * @param configuration active framework configuration
 * @param registry registry providing registered data sources
 */
public record TestContext(
    Class<?> testClass,
    Method testMethod,
    Configuration configuration,
    DataSourceRegistry registry) {

  /**
   * Compact constructor with validation.
   *
   * @throws NullPointerException if any parameter is null
   */
  public TestContext {
    Objects.requireNonNull(testClass, "testClass must not be null");
    Objects.requireNonNull(testMethod, "testMethod must not be null");
    Objects.requireNonNull(configuration, "configuration must not be null");
    Objects.requireNonNull(registry, "registry must not be null");
  }

  /**
   * Factory method that extracts the relevant information from a JUnit {@link ExtensionContext}.
   *
   * @param extensionContext active JUnit extension context for the running test
   * @param configuration resolved framework configuration
   * @param registry registry containing configured data sources
   * @return an immutable context capturing the current test state
   * @throws NullPointerException if any parameter is null
   */
  public static TestContext from(
      final ExtensionContext extensionContext,
      final Configuration configuration,
      final DataSourceRegistry registry) {
    Objects.requireNonNull(extensionContext, "extensionContext must not be null");
    Objects.requireNonNull(configuration, "configuration must not be null");
    Objects.requireNonNull(registry, "registry must not be null");

    final var testClass = extensionContext.getRequiredTestClass();
    final var testMethod = extensionContext.getRequiredTestMethod();
    return new TestContext(testClass, testMethod, configuration, registry);
  }
}
