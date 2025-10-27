/**
 * Domain value objects for type-safe database testing primitives.
 *
 * <p>This package contains immutable value objects that represent core domain concepts in the
 * database testing framework. These types provide:
 *
 * <ul>
 *   <li><strong>Type Safety:</strong> Prevent mixing different string types (table names, column
 *       names, etc.)
 *   <li><strong>Validation:</strong> Enforce naming rules and constraints at creation time
 *   <li><strong>Immutability:</strong> Thread-safe by design with no mutable state
 *   <li><strong>Intent Clarity:</strong> Self-documenting code through expressive type names
 * </ul>
 *
 * <h2>Core Value Objects</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.TableName} - Database table
 *       identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.ColumnName} - Table column identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.SchemaName} - Database schema
 *       identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.ScenarioName} - Test scenario
 *       identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.ScenarioMarker} - Scenario marker
 *       column identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.DataSourceName} - Data source
 *       identifier
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain.DataValue} - Cell value wrapper
 *       (nullable)
 * </ul>
 *
 * <h2>Design Principles</h2>
 *
 * <p>All value objects in this package follow these principles:
 *
 * <ul>
 *   <li><strong>Records:</strong> Implemented as Java records for concise immutable data carriers
 *   <li><strong>Validation:</strong> Compact constructors enforce invariants (non-null, non-blank)
 *   <li><strong>No Runtime Checks:</strong> Internal package relies on NullAway compile-time safety
 *   <li><strong>Value Semantics:</strong> Equality based on content, not identity
 * </ul>
 *
 * <h2>Internal Package</h2>
 *
 * <p>This package is INTERNAL implementation. External users should not directly use these types.
 * The public API ({@code api/} package) uses plain strings and collections for simplicity.
 *
 * @see io.github.seijikohara.dbtester.internal
 */
package io.github.seijikohara.dbtester.internal.domain;
