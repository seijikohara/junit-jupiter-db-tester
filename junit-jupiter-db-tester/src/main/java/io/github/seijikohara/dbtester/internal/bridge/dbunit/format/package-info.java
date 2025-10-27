/**
 * Format-specific dataset readers implementing the Strategy pattern.
 *
 * <p>This package contains implementations for reading test datasets from various file formats. The
 * {@link io.github.seijikohara.dbtester.internal.bridge.dbunit.format.DataSetReader} interface
 * defines the Strategy pattern contract, allowing the framework to support multiple formats without
 * modifying core logic.
 *
 * <h2>Design Pattern: Strategy</h2>
 *
 * <p>Each file format (CSV, JSON, YAML, XML) has its own reader implementation. The strategy
 * pattern allows runtime selection of the appropriate reader based on file format requirements.
 *
 * <h2>Current Implementations</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.bridge.dbunit.format.CsvDataSetReader} - CSV
 *       file format using DbUnit's CSV parser
 * </ul>
 *
 * <h2>Future Implementations</h2>
 *
 * <ul>
 *   <li>JsonDataSetReader - JSON file format
 *   <li>YamlDataSetReader - YAML file format
 *   <li>XmlDataSetReader - XML file format
 * </ul>
 *
 * <h2>DbUnit Isolation</h2>
 *
 * <p>Format readers are the entry point for external data into the framework. They use DbUnit's
 * parsing capabilities internally but expose only framework types ({@link
 * io.github.seijikohara.dbtester.internal.dataset.DataSet}) externally.
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.DatabaseBridge
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter
 */
package io.github.seijikohara.dbtester.internal.bridge.dbunit.format;
