package io.github.seijikohara.dbtester.spring.autoconfigure;

import io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension;
import java.util.Objects;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Spring Boot-aware database test extension that automatically registers DataSources.
 *
 * <p>This extension extends {@link DatabaseTestExtension} and adds automatic DataSource
 * registration from the Spring {@link ApplicationContext}. When the {@code
 * dbtester.auto-register-data-sources} property is set to {@code true} (the default), all
 * Spring-managed {@link javax.sql.DataSource} beans are automatically registered with the {@link
 * io.github.seijikohara.dbtester.api.config.DataSourceRegistry} before any test execution.
 *
 * <h2>Usage</h2>
 *
 * <p>Replace {@code @ExtendWith(DatabaseTestExtension.class)} with this extension:
 *
 * <pre>{@code
 * @SpringBootTest
 * @ExtendWith(SpringBootDatabaseTestExtension.class)
 * class MyRepositoryTest {
 *
 *     // No @BeforeAll setup needed - DataSources are registered automatically
 *
 *     @Preparation
 *     @Expectation
 *     @Test
 *     void testCreate() {
 *         // Test logic
 *     }
 * }
 * }</pre>
 *
 * <h2>Configuration</h2>
 *
 * <p>Configure automatic registration in {@code application.properties}:
 *
 * <pre>{@code
 * # Enable auto-configuration (default: true)
 * dbtester.enabled=true
 *
 * # Enable automatic DataSource registration (default: true)
 * dbtester.auto-register-data-sources=true
 * }</pre>
 *
 * <h2>Manual Registration</h2>
 *
 * <p>If you need to customize DataSource registration (e.g., for multiple DataSources with specific
 * names), you can still use the parent class {@link DatabaseTestExtension} directly and perform
 * manual registration in {@code @BeforeAll}.
 *
 * @see DatabaseTestExtension
 * @see DataSourceRegistrar
 * @see DatabaseTesterAutoConfiguration
 */
public class SpringBootDatabaseTestExtension extends DatabaseTestExtension
    implements BeforeAllCallback {

  /** Logger for tracking automatic DataSource registration. */
  private static final Logger logger =
      LoggerFactory.getLogger(SpringBootDatabaseTestExtension.class);

  /**
   * Creates a new Spring Boot database test extension.
   *
   * <p>This constructor is called by JUnit Jupiter when the extension is registered via
   * {@code @ExtendWith}.
   */
  public SpringBootDatabaseTestExtension() {
    super();
  }

  /**
   * Automatically registers Spring-managed DataSources before all tests in a test class.
   *
   * <p>This method:
   *
   * <ol>
   *   <li>Retrieves the Spring {@link ApplicationContext} using {@link SpringExtension}
   *   <li>Checks if {@link DataSourceRegistrar} is available in the context
   *   <li>If available and auto-registration is enabled, registers all DataSources with the {@link
   *       io.github.seijikohara.dbtester.api.config.DataSourceRegistry}
   * </ol>
   *
   * <p>If the Spring context is not available (e.g., non-Spring tests) or if {@link
   * DataSourceRegistrar} is not configured, this method silently skips automatic registration,
   * allowing the extension to work in both Spring and non-Spring environments.
   *
   * @param context the extension context for the test class
   */
  @Override
  public void beforeAll(final ExtensionContext context) {
    Objects.requireNonNull(context, "context must not be null");

    try {
      final var applicationContext = SpringExtension.getApplicationContext(context);
      registerDataSourcesFromContext(context, applicationContext);
    } catch (final IllegalStateException e) {
      // Spring context not available - this is expected for non-Spring tests
      logger.debug(
          "Spring ApplicationContext not available, skipping automatic DataSource registration: {}",
          e.getMessage());
    }
  }

  /**
   * Registers DataSources from the Spring ApplicationContext.
   *
   * @param context the extension context
   * @param applicationContext the Spring application context
   */
  private void registerDataSourcesFromContext(
      final ExtensionContext context, final ApplicationContext applicationContext) {

    if (!applicationContext.containsBean("dataSourceRegistrar")) {
      logger.debug(
          "DataSourceRegistrar bean not found in ApplicationContext, "
              + "skipping automatic DataSource registration");
      return;
    }

    final var registrar = applicationContext.getBean(DataSourceRegistrar.class);
    final var registry = getRegistry(context);

    logger.info("Automatically registering Spring DataSources with database testing framework");
    registrar.registerAll(registry);
    logger.info("Automatic DataSource registration completed");
  }
}
