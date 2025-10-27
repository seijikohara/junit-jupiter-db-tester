package io.github.seijikohara.dbtester.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Configures a CSV-based dataset for database test preparation or expectation validation.
 *
 * <p>This annotation defines how CSV files are loaded and applied to the database. It specifies the
 * data source, resource location, and scenario filtering.
 *
 * <p>This annotation cannot be applied directly to test classes or methods. Instead, it must be
 * used as an element within {@link Preparation#dataSets()} or {@link Expectation#dataSets()}
 * arrays.
 *
 * <h2>Convention-Based Resolution</h2>
 *
 * <p>When {@link #resourceLocation()} is not specified, CSV files are resolved using conventions:
 *
 * <ul>
 *   <li>For {@link Preparation}: {@code classpath:[test-class-package]/[test-class-name]/}
 *   <li>For {@link Expectation}: {@code classpath:[test-class-package]/[test-class-name]/expected/}
 * </ul>
 *
 * <p>When {@link #scenarioNames()} is empty, the test method name is used as the scenario filter,
 * loading only rows that match the scenario column value.
 *
 * @see Preparation
 * @see Expectation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSet {

  /**
   * Specifies the name of the data source to use for this dataset.
   *
   * <p>The data source must be registered in the {@link
   * io.github.seijikohara.dbtester.config.DataSourceRegistry} before test execution. An empty
   * string, {@code null}, or {@code "dataSource"} all refer to the default data source.
   *
   * @return the data source name; empty string for the default data source
   */
  String dataSourceName() default "";

  /**
   * Specifies the directory containing CSV dataset files.
   *
   * <p>Both classpath and file system paths are supported:
   *
   * <ul>
   *   <li>Classpath: {@code "classpath:com/example/testdata/"}
   *   <li>File system: {@code "/absolute/path/to/testdata/"}
   * </ul>
   *
   * <p>When empty, the framework uses convention-based resolution to locate CSV files based on the
   * test class and method names.
   *
   * @return the resource location; empty string for convention-based resolution
   */
  String resourceLocation() default "";

  /**
   * Specifies scenario names to filter rows from CSV files.
   *
   * <p>When CSV files contain a scenario marker column (configured via {@link
   * io.github.seijikohara.dbtester.config.ConventionSettings#scenarioMarker()}), only rows matching
   * one of these scenario names are loaded. This allows multiple test methods to share the same CSV
   * files while loading different subsets of data.
   *
   * <p>When empty, the test method name is used as the scenario name.
   *
   * @return array of scenario names; empty array to use the test method name
   */
  String[] scenarioNames() default {};
}
