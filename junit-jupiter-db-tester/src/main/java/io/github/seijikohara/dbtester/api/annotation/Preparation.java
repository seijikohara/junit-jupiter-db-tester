package io.github.seijikohara.dbtester.api.annotation;

import io.github.seijikohara.dbtester.api.operation.Operation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prepares the database state by loading CSV datasets before test execution.
 *
 * <p>This annotation loads one or more CSV-based datasets into the database, establishing the
 * initial state required for the test. The framework applies the specified database operation
 * (default: {@link Operation#CLEAN_INSERT}) to each dataset, ensuring a consistent and predictable
 * test environment.
 *
 * <h2>Scope and Inheritance</h2>
 *
 * <p>This annotation can be applied at both the method and class levels:
 *
 * <ul>
 *   <li>Method level: affects only the annotated test method
 *   <li>Class level: affects all test methods in the class
 * </ul>
 *
 * <p>Method-level annotations override class-level annotations. The annotation is inherited by
 * subclasses unless the subclass provides its own {@code @Preparation} annotation.
 *
 * <h2>Convention-Based Resolution</h2>
 *
 * <p>When {@link DataSet#resourceLocation()} is not specified, CSV files are automatically resolved
 * from {@code classpath:[test-class-package]/[test-class-name]/}.
 *
 * @see DataSet
 * @see Expectation
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Preparation {

  /**
   * Specifies the datasets to load into the database before test execution.
   *
   * <p>Each dataset is loaded in the order specified. When empty, the framework uses
   * convention-based resolution to locate CSV files, using the test method name as the scenario
   * filter.
   *
   * @return array of datasets to load; empty array for convention-based resolution
   */
  DataSet[] dataSets() default {};

  /**
   * Specifies the database operation for all datasets.
   *
   * <p>This operation is applied to all datasets in {@link #dataSets()} during test preparation.
   *
   * @return the database operation
   * @see Operation
   */
  Operation operation() default Operation.CLEAN_INSERT;
}
