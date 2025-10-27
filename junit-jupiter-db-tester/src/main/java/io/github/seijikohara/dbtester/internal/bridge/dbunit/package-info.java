/**
 * Bridge layer isolating DbUnit dependencies from the framework.
 *
 * <p>This package implements the <strong>Bridge pattern</strong> to completely isolate DbUnit
 * dependencies from the rest of the framework. The public entry point is {@link
 * io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge}, which provides unified
 * access to DbUnit operations, assertions, and dataset loading.
 *
 * <h2>Design Pattern: Bridge</h2>
 *
 * <p>The Bridge pattern separates the framework's abstraction (domain types like DataSet, Table,
 * Row) from the implementation (DbUnit types like IDataSet, ITable). This allows either side to
 * evolve independently.
 *
 * <pre>
 * Framework Domain Types           DbUnit Types
 * (Abstraction)                    (Implementation)
 *       |                                |
 *       └────── DatabaseBridge ──────────┘
 *              (Bridge)
 * </pre>
 *
 * <h2>Package Structure</h2>
 *
 * <p>The bridge package is organized into focused sub-packages:
 *
 * <ul>
 *   <li><strong>Root</strong> - Core bridge components
 *       <ul>
 *         <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge} - Public
 *             entry point (only public class)
 *         <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.TypeConverter} - Type
 *             conversion (Framework → DbUnit)
 *       </ul>
 *   <li><strong>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.format
 *       format/}</strong> - Strategy pattern for file format readers (CSV, JSON, YAML, XML)
 *   <li><strong>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter
 *       adapter/}</strong> - Adapter pattern for DbUnit → Framework type wrapping
 *   <li><strong>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion
 *       assertion/}</strong> - Assertion delegation and utilities
 * </ul>
 *
 * <h2>Type Conversion</h2>
 *
 * <p>Type conversion flows in two directions:
 *
 * <ul>
 *   <li><strong>Framework → DbUnit</strong>: {@link
 *       io.github.seijikohara.dbtester.internal.bridge.dbunit.TypeConverter} converts framework
 *       types to DbUnit types for operations and assertions
 *   <li><strong>DbUnit → Framework</strong>: {@link
 *       io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter Adapter classes} wrap DbUnit
 *       types as framework types when loading datasets
 * </ul>
 *
 * <h2>Visibility and Encapsulation</h2>
 *
 * <p><strong>Only {@link io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge} is
 * public.</strong> All other classes are package-private, enforcing that all DbUnit access goes
 * through the bridge. This ensures complete isolation of DbUnit from the framework.
 *
 * <h2>Core Capabilities</h2>
 *
 * <ul>
 *   <li><strong>Operations</strong>: Database operations (INSERT, UPDATE, DELETE, CLEAN_INSERT,
 *       etc.)
 *   <li><strong>Assertions</strong>: Dataset and table comparisons with flexible filtering
 *   <li><strong>Loading</strong>: Format-specific dataset readers (CSV currently, JSON/YAML/XML
 *       future)
 *   <li><strong>Verification</strong>: Database state validation against expected datasets
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * // Get singleton bridge instance
 * DatabaseBridge bridge = DatabaseBridge.getInstance();
 *
 * // Load CSV dataset
 * DataSet dataSet = bridge.loadCsvDataSet(Path.of("test-data"));
 *
 * // Execute database operation
 * bridge.executeOperation(scenarioDataSet, Operation.CLEAN_INSERT, dataSource);
 *
 * // Verify database state
 * bridge.verifyExpectation(expectedDataSet, dataSource);
 *
 * // Assert dataset equality
 * bridge.assertEquals(expected, actual);
 * }</pre>
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.format
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion
 */
package io.github.seijikohara.dbtester.internal.bridge.dbunit;
