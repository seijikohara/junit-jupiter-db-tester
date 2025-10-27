/**
 * Database-agnostic dataset abstractions.
 *
 * <p>This package defines the core abstractions for representing test data in a
 * database-independent way. These interfaces decouple the framework from specific database testing
 * libraries (like DbUnit) and allow for future extensibility.
 *
 * <h2>Core Abstractions</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.DataSet} - Collection of tables
 *       representing database state
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.Table} - Single database table with
 *       rows and columns
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.Row} - Single row within a table
 * </ul>
 *
 * <h2>Adapter Pattern</h2>
 *
 * <p>These abstractions are implemented by adapter classes that wrap DbUnit types. Concrete
 * implementations are in the adapter sub-package:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitDataSetAdapter}
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitTableAdapter}
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter.DbUnitRowAdapter}
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter
 */
package io.github.seijikohara.dbtester.internal.dataset;
