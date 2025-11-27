/**
 * Coordination layer that adapts JUnit callbacks to the framework's database lifecycle.
 *
 * <p>The package houses the internal {@link
 * io.github.seijikohara.dbtester.internal.junit.lifecycle.TestLifecycle} orchestrator together with
 * lightweight collaborators that execute preparation datasets and validate expectations. These
 * types are not part of the public API; they are invoked exclusively from {@link
 * io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension}.
 */
package io.github.seijikohara.dbtester.internal.junit.lifecycle;
