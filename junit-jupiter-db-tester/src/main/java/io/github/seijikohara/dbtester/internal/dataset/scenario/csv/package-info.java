/**
 * CSV format implementation for scenario-based datasets.
 *
 * <p>This package provides Collection-based implementations for loading CSV (Comma-Separated
 * Values) files with scenario filtering support. It is a framework feature independent of DbUnit.
 *
 * <h2>CSV File Structure</h2>
 *
 * <p>CSV files should follow these conventions:
 *
 * <ul>
 *   <li>File name matches the table name (e.g., {@code USERS.csv})
 *   <li>First row contains column headers
 *   <li>Optional {@code #scenario} column for scenario-based filtering
 *   <li>Empty cells represent NULL values in the database
 *   <li>Standard CSV format with comma separators
 * </ul>
 *
 * <h2>Core Classes</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioDataSet} -
 *       CSV dataset with scenario filtering
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioTable} - CSV
 *       table with scenario filtering
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvFormatProvider} -
 *       Format provider for CSV files
 * </ul>
 *
 * <h2>Design Principles</h2>
 *
 * <ul>
 *   <li><b>Framework Independence</b>: No DbUnit dependencies, uses Collection-based APIs
 *   <li><b>Scenario Filtering</b>: Filters rows based on scenario marker column
 *   <li><b>Null Handling</b>: Empty CSV cells convert to null for database compatibility
 *   <li><b>Auto-Registration</b>: CsvFormatProvider automatically registered via reflection
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario
 */
package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;
