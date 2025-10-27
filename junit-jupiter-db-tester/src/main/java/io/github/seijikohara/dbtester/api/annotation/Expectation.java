package io.github.seijikohara.dbtester.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates the database state against expected CSV datasets after test execution.
 *
 * <p>This annotation defines expected database content that is verified after the test completes.
 * The framework reads the current database state and compares it against the provided CSV datasets,
 * performing row-by-row and column-by-column validation. Any discrepancies result in a test
 * failure.
 *
 * <p>Unlike {@link Preparation}, expectation validation is read-only and does not modify the
 * database.
 *
 * <h2>Scope and Inheritance</h2>
 *
 * <p>This annotation can be applied at both the method and class levels:
 *
 * <ul>
 *   <li>Method level: validates only after the annotated test method
 *   <li>Class level: validates after all test methods in the class
 * </ul>
 *
 * <p>Method-level annotations override class-level annotations. The annotation is inherited by
 * subclasses unless the subclass provides its own {@code @Expectation} annotation.
 *
 * <h2>Convention-Based Resolution</h2>
 *
 * <p>When {@link DataSet#resourceLocation()} is not specified, CSV files are automatically resolved
 * from {@code classpath:[test-class-package]/[test-class-name]/expected/}.
 *
 * @see DataSet
 * @see Preparation
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Expectation {

  /**
   * Specifies the expected datasets to validate against the actual database state.
   *
   * <p>Each dataset is validated in the order specified. When empty, the framework uses
   * convention-based resolution to locate CSV files, using the test method name as the scenario
   * filter.
   *
   * @return array of expected datasets; empty array for convention-based resolution
   */
  DataSet[] dataSets() default {};
}
