/**
 * Public API for database testing with JUnit Jupiter.
 *
 * <p>This package provides the primary interface for test code:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.api.annotation}: Test annotations
 *       ({@code @Preparation}, {@code @Expectation}, {@code @DataSet})
 *   <li>{@link io.github.seijikohara.dbtester.api.assertion}: Programmatic database assertions
 *   <li>{@link io.github.seijikohara.dbtester.api.operation}: Database operations (CLEAN_INSERT,
 *       INSERT, etc.)
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 * @see io.github.seijikohara.dbtester.api.assertion.DatabaseAssertion
 * @see io.github.seijikohara.dbtester.api.operation.Operation
 */
package io.github.seijikohara.dbtester.api;
