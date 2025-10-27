/**
 * Configuration and data source registry components.
 *
 * <p>This package provides framework configuration and data source management:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.config.Configuration}: Immutable framework
 *       configuration with convention settings and operation defaults
 *   <li>{@link io.github.seijikohara.dbtester.config.DataSourceRegistry}: Thread-safe mutable
 *       registry for managing data sources
 *   <li>{@link io.github.seijikohara.dbtester.config.ConventionSettings}: Convention-based file
 *       resolution settings
 *   <li>{@link io.github.seijikohara.dbtester.config.OperationDefaults}: Default database
 *       operations for preparation and expectation phases
 * </ul>
 *
 * <p>Configuration classes are immutable for thread safety. The registry is explicitly mutable to
 * support runtime data source registration in test setup methods.
 *
 * @see io.github.seijikohara.dbtester.config.Configuration
 * @see io.github.seijikohara.dbtester.config.DataSourceRegistry
 * @see io.github.seijikohara.dbtester.config.ConventionSettings
 * @see io.github.seijikohara.dbtester.config.OperationDefaults
 */
package io.github.seijikohara.dbtester.config;
