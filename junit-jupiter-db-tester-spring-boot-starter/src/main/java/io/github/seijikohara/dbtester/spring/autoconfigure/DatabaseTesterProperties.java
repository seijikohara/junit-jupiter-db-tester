package io.github.seijikohara.dbtester.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for JUnit Jupiter DB Tester Spring Boot integration.
 *
 * <p>These properties control how DataSource beans from the Spring application context are
 * registered with the {@link io.github.seijikohara.dbtester.api.config.DataSourceRegistry}.
 *
 * <h2>Configuration Example</h2>
 *
 * <pre>{@code
 * # application.yml
 * dbtester:
 *   enabled: true
 *   auto-register-data-sources: true
 * }</pre>
 *
 * @param enabled whether the DB Tester integration is enabled (defaults to true)
 * @param autoRegisterDataSources whether to automatically register DataSource beans with the
 *     DataSourceRegistry (defaults to true)
 */
@ConfigurationProperties(prefix = "dbtester")
public record DatabaseTesterProperties(
    @DefaultValue("true") boolean enabled, @DefaultValue("true") boolean autoRegisterDataSources) {}
