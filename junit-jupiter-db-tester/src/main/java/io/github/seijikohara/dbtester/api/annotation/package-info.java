/**
 * Test annotations for declarative database testing.
 *
 * <p>This package provides annotations for automatic database test setup and validation:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.api.annotation.Preparation @Preparation}: Loads CSV
 *       datasets before test execution
 *   <li>{@link io.github.seijikohara.dbtester.api.annotation.Expectation @Expectation}: Validates
 *       database state after test execution
 *   <li>{@link io.github.seijikohara.dbtester.api.annotation.DataSet @DataSet}: Configures
 *       individual datasets
 * </ul>
 *
 * <p>CSV files are resolved by convention: {@code classpath:[TestClass]/[table].csv} for
 * preparation, {@code classpath:[TestClass]/expected/[table].csv} for expectation. Custom
 * locations, scenario filtering, and database operations can be configured via annotation
 * attributes.
 *
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 * @see io.github.seijikohara.dbtester.api.operation.Operation
 */
package io.github.seijikohara.dbtester.api.annotation;
