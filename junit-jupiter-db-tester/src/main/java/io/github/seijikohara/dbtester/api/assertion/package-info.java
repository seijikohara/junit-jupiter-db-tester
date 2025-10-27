/**
 * Programmatic database assertion API for custom validation logic.
 *
 * <p>This package provides programmatic assertion utilities for validating database state. While
 * {@link io.github.seijikohara.dbtester.api.annotation.Expectation @Expectation} annotation handles
 * declarative validation, this API enables dynamic assertions, custom queries, column filtering,
 * and mid-test validations.
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.api.assertion.DatabaseAssertion}: Static facade for
 *       database assertions
 *   <li>{@link io.github.seijikohara.dbtester.api.assertion.AssertionFailureHandler}: Custom
 *       failure handler for assertion failures
 * </ul>
 *
 * <p>Use programmatic assertions when you need dynamic validation logic, custom SQL queries, column
 * filtering, or multiple validation points within a single test. All methods operate on immutable
 * datasets and are thread-safe.
 *
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 * @see org.dbunit.assertion.DbUnitAssert
 */
package io.github.seijikohara.dbtester.api.assertion;
