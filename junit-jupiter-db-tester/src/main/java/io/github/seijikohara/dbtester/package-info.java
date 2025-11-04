/**
 * JUnit Jupiter extension for declarative database testing with scenario-aware datasets.
 *
 * <p>The framework layers a convention-oriented experience on top of DbUnit. Test authors express
 * intent through annotations and high-level APIs, while the internal bridge isolates direct DbUnit
 * usage and maintains compatibility guarantees for clients.
 *
 * <h2>Highlights</h2>
 *
 * <ul>
 *   <li>Annotation-driven preparation and verification ({@link
 *       io.github.seijikohara.dbtester.api.annotation.Preparation}, {@link
 *       io.github.seijikohara.dbtester.api.annotation.Expectation}) with automatic CSV discovery.
 *   <li>Scenario filtering so a single dataset can back multiple tests via {@code [Scenario]}
 *       marker columns.
 *   <li>Multi-database support through {@link
 *       io.github.seijikohara.dbtester.api.config.DataSourceRegistry} and {@link
 *       io.github.seijikohara.dbtester.api.domain.DataSourceName} value types.
 *   <li>Programmatic assertions supplied by {@link
 *       io.github.seijikohara.dbtester.api.assertion.DatabaseAssertion} for advanced verification
 *       flows.
 *   <li>Strict null-safety defaults enforced by NullAway compile-time checking and runtime
 *       validation within the public API boundary.
 * </ul>
 *
 * <h2>Package Overview</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.api}: Public surface area including annotations,
 *       configuration, dataset abstractions, and assertion facilities.
 *   <li>{@link io.github.seijikohara.dbtester.internal}: Implementation-only packages that remain
 *       encapsulated behind the bridge pattern and may evolve independently.
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.api.extension.DatabaseTestExtension
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 * @see io.github.seijikohara.dbtester.api.config.DataSourceRegistry
 */
package io.github.seijikohara.dbtester;
