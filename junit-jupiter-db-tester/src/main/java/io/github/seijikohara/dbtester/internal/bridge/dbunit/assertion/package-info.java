/**
 * Assertion utilities and DbUnit assertion delegation.
 *
 * <p>This package contains internal utilities for database assertion operations, isolating DbUnit's
 * assertion APIs from the framework. All assertion logic is delegated to DbUnit while maintaining
 * framework type safety.
 *
 * <h2>Design Pattern: Delegate</h2>
 *
 * <p>The assertion classes use the Delegate pattern to wrap DbUnit's assertion capabilities. {@link
 * io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.DatabaseAssert} provides the
 * primary delegation to DbUnit's assertion APIs.
 *
 * <h2>Key Components</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.DatabaseAssert} -
 *       Core assertion delegation to DbUnit
 *   <li>{@link
 *       io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.FailureHandlerAdapter} -
 *       Converts framework failure handlers to DbUnit format
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.ColumnFilter} -
 *       Column filtering utilities for flexible comparison
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.DataSetComparator} -
 *       Dataset comparison logic
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion.TableComparator} -
 *       Table comparison logic
 * </ul>
 *
 * <h2>Assertion Capabilities</h2>
 *
 * <ul>
 *   <li>Dataset and table equality assertions
 *   <li>Column filtering for ignoring auto-generated fields
 *   <li>Query-based comparisons for complex scenarios
 *   <li>Custom failure handler support
 *   <li>Enhanced error reporting with row/column details
 * </ul>
 *
 * <h2>Visibility</h2>
 *
 * <p>All classes in this package are package-private. Assertion operations must go through {@link
 * io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge}, which provides the public
 * API.
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge
 * @see org.dbunit.assertion.DbUnitAssert
 */
package io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion;
