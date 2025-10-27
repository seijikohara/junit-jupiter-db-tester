/**
 * Internal test lifecycle management implementation.
 *
 * <p>This package contains the internal implementation of test lifecycle orchestration, including
 * preparation execution and expectation verification. All classes in this package are
 * package-private and not intended for direct use.
 *
 * <h2>Key Components</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.junit.lifecycle.TestLifecycle} - Lifecycle
 *       orchestrator
 *   <li>{@link io.github.seijikohara.dbtester.internal.junit.lifecycle.TestContext} - Immutable
 *       test context
 *   <li>{@link io.github.seijikohara.dbtester.internal.junit.lifecycle.PreparationExecutor} -
 *       Preparation dataset execution
 *   <li>{@link io.github.seijikohara.dbtester.internal.junit.lifecycle.ExpectationVerifier} -
 *       Expectation dataset verification
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 */
package io.github.seijikohara.dbtester.internal.junit.lifecycle;
