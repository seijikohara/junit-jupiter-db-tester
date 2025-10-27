/**
 * JUnit Jupiter extension integration.
 *
 * <p>This package provides the JUnit Jupiter extension that integrates the database testing
 * framework with the test lifecycle:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.extension.DatabaseTestExtension}: Main extension for
 *       processing {@code @Preparation} and {@code @Expectation} annotations
 * </ul>
 *
 * <p>Tests use this extension via {@code @ExtendWith(DatabaseTestExtension.class)} to enable
 * automatic database preparation and validation.
 *
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 */
package io.github.seijikohara.dbtester.extension;
