/**
 * Framework-independent scenario-based dataset filtering.
 *
 * <p>This package provides Collection-based dataset implementations that support scenario-based
 * filtering. Scenario filtering allows organizing multiple test scenarios within the same data
 * files by using a special marker column (e.g., {@code #scenario}).
 *
 * <h2>Core Abstractions</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet} - Abstract
 *       dataset with scenario filtering
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioTable} - Abstract
 *       table with scenario filtering
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.TableOrderingResolver} - Utility for
 *       resolving table-ordering.txt files
 * </ul>
 *
 * <h2>Format Provider System</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatProvider} -
 *       Interface for format providers
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.DataSetFormatRegistry} -
 *       Auto-registration registry for providers
 * </ul>
 *
 * <h2>Design Principles</h2>
 *
 * <ul>
 *   <li><b>Framework Independence</b>: No DbUnit dependencies, uses Collection-based APIs
 *   <li><b>Scenario Filtering</b>: Framework feature for organizing test scenarios
 *   <li><b>Extensibility</b>: Format-specific implementations in subpackages (e.g., {@code csv/})
 *   <li><b>Auto-Discovery</b>: Format providers automatically registered via reflection
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.dataset
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv
 */
package io.github.seijikohara.dbtester.internal.dataset.scenario;
