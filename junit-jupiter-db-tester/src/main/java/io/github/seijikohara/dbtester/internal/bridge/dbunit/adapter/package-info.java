/**
 * Adapters converting DbUnit types to framework types.
 *
 * <p>This package contains Adapter pattern implementations that wrap DbUnit types (IDataSet,
 * ITable) and expose them as framework types (DataSet, Table, Row). This conversion flows in one
 * direction: <strong>DbUnit → Framework</strong>.
 *
 * <h2>Design Pattern: Adapter</h2>
 *
 * <p>The Adapter pattern isolates DbUnit's array-based APIs from the framework's Collection-based
 * abstractions. Framework code never sees DbUnit types directly; all DbUnit data structures are
 * wrapped by these adapters.
 *
 * <h2>Key Adapters</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitDataSetAdapter}
 *       - Wraps IDataSet as DataSet
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitTableAdapter} -
 *       Wraps ITable as Table
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitRowAdapter} -
 *       Wraps ITable row access as Row
 * </ul>
 *
 * <h2>Conversion Direction</h2>
 *
 * <p>These adapters convert <strong>DbUnit → Framework</strong>. For the opposite direction
 * (Framework → DbUnit), see {@link
 * io.github.seijikohara.dbtester.internal.bridge.dbunit.TypeConverter}.
 *
 * <h2>Usage Context</h2>
 *
 * <p>Adapters are primarily used when:
 *
 * <ul>
 *   <li>Loading datasets from files (CSV, JSON, etc.)
 *   <li>Converting DbUnit's parsed data to framework types
 *   <li>Isolating DbUnit's implementation details from framework code
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.TypeConverter
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.format
 * @see io.github.seijikohara.dbtester.internal.dataset
 */
package io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter;
