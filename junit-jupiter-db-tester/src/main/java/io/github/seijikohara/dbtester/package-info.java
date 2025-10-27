/**
 * JUnit Jupiter database testing framework with CSV-based data management.
 *
 * <p>This framework provides annotation-driven database testing capabilities for JUnit Jupiter,
 * built on top of DbUnit. It simplifies database test development through convention-based file
 * resolution, declarative annotations, and comprehensive data management features.
 *
 * <h2>Key Features</h2>
 *
 * <ul>
 *   <li><strong>Convention-Based Resolution:</strong> Automatically locates CSV files based on test
 *       class and method names
 *   <li><strong>Scenario Filtering:</strong> Supports filtering rows by scenario markers for
 *       sharing datasets across tests
 *   <li><strong>Multi-Database Support:</strong> Manages multiple named data sources within a
 *       single test class
 *   <li><strong>Declarative Annotations:</strong> {@link
 *       io.github.seijikohara.dbtester.api.annotation.Preparation @Preparation} and {@link
 *       io.github.seijikohara.dbtester.api.annotation.Expectation @Expectation} for clean test
 *       structure
 *   <li><strong>Flexible Operations:</strong> Full DbUnit operation support (INSERT, UPDATE,
 *       CLEAN_INSERT, etc.)
 *   <li><strong>Null Safety:</strong> Comprehensive null safety annotations using JSpecify
 * </ul>
 *
 * <h2>Package Structure</h2>
 *
 * <ul>
 *   <li><strong>{@link io.github.seijikohara.dbtester.api}:</strong> Public API for test code
 *       <ul>
 *         <li>{@link io.github.seijikohara.dbtester.api.annotation}: Declarative test annotations
 *         <li>{@link io.github.seijikohara.dbtester.api.assertion}: Programmatic assertion API
 *         <li>{@link io.github.seijikohara.dbtester.api.operation}: Database operation definitions
 *       </ul>
 *   <li><strong>{@link io.github.seijikohara.dbtester.config}:</strong> Configuration and data
 *       source management
 *   <li><strong>{@link io.github.seijikohara.dbtester.extension}:</strong> JUnit Jupiter extension
 *       integration
 *   <li><strong>{@link io.github.seijikohara.dbtester.exception}:</strong> Framework exception
 *       hierarchy
 *   <li><strong>{@link io.github.seijikohara.dbtester.internal}:</strong> Internal implementation
 *       (subject to change)
 * </ul>
 *
 * <h2>Quick Start</h2>
 *
 * <pre>{@code
 * @ExtendWith(DatabaseTestExtension.class)
 * class UserServiceTest {
 *
 *   @BeforeAll
 *   static void setup(ExtensionContext context) {
 *     DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
 *     registry.registerDefault(createDataSource());
 *   }
 *
 *   @Test
 *   @Preparation  // Loads CSV from classpath:[package]/UserServiceTest/
 *   @Expectation  // Validates against classpath:[package]/UserServiceTest/expected/
 *   void shouldCreateUser() {
 *     // Test implementation
 *   }
 * }
 * }</pre>
 *
 * @see io.github.seijikohara.dbtester.extension.DatabaseTestExtension
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 * @see io.github.seijikohara.dbtester.config.DataSourceRegistry
 */
@NullMarked
package io.github.seijikohara.dbtester;

import org.jspecify.annotations.NullMarked;
