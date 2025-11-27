package io.github.seijikohara.dbtester.spring.autoconfigure;

import io.github.seijikohara.dbtester.api.config.DataSourceRegistry;
import io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for JUnit Jupiter DB Tester Spring Boot integration.
 *
 * <p>This auto-configuration is activated when:
 *
 * <ul>
 *   <li>{@link DataSourceRegistry} is on the classpath
 *   <li>{@code dbtester.enabled} property is true (default)
 * </ul>
 *
 * <p>The configuration provides a {@link DataSourceRegistrar} bean that automatically registers
 * Spring-managed {@link DataSource} beans with the {@link DataSourceRegistry} used by the database
 * testing framework.
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * @SpringBootTest
 * @ExtendWith(DatabaseTestExtension.class)
 * class MyRepositoryTest {
 *
 *     // DataSource from Spring context is automatically registered
 *
 *     @Autowired
 *     private DataSourceRegistrar registrar;
 *
 *     @BeforeAll
 *     static void setup(ExtensionContext context) {
 *         // Get the registry and registrar will populate it
 *         DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
 *     }
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
 * @see DataSourceRegistry
 * @see DatabaseTestExtension
 * @see DataSourceRegistrar
 */
@AutoConfiguration
@ConditionalOnClass(DataSourceRegistry.class)
@ConditionalOnProperty(
    prefix = "dbtester",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(DatabaseTesterProperties.class)
public class DatabaseTesterAutoConfiguration {

  /**
   * Creates a new auto-configuration instance.
   *
   * <p>This constructor is called by Spring Boot's auto-configuration mechanism.
   */
  public DatabaseTesterAutoConfiguration() {
    // Default constructor for Spring auto-configuration
  }

  /**
   * Creates a {@link DataSourceRegistrar} bean.
   *
   * <p>The registrar is responsible for registering Spring-managed {@link DataSource} beans with
   * the {@link DataSourceRegistry}. It provides a bridge between the Spring application context and
   * the database testing framework.
   *
   * @param properties the database tester configuration properties
   * @return a new DataSourceRegistrar instance
   */
  @Bean
  public DataSourceRegistrar dataSourceRegistrar(final DatabaseTesterProperties properties) {
    return new DataSourceRegistrar(properties);
  }
}
