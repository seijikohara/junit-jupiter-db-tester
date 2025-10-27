/**
 * Exception hierarchy for database testing framework errors.
 *
 * <p>All exceptions extend {@link io.github.seijikohara.dbtester.exception.DatabaseTesterException}
 * as the base unchecked exception:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.exception.ConfigurationException} - Framework
 *       initialization failures
 *   <li>{@link io.github.seijikohara.dbtester.exception.DataSetLoadException} - Dataset loading
 *       failures
 *   <li>{@link io.github.seijikohara.dbtester.exception.DataSourceNotFoundException} - Missing data
 *       source registration
 *   <li>{@link io.github.seijikohara.dbtester.exception.ValidationException} - Assertion failures
 * </ul>
 *
 * <p>These unchecked exceptions are designed to fail tests fast with detailed error messages
 * including file paths, table names, and data source names. All exceptions support exception
 * chaining to preserve full stack traces.
 *
 * @see io.github.seijikohara.dbtester.exception.DatabaseTesterException
 * @see io.github.seijikohara.dbtester.exception.ConfigurationException
 * @see io.github.seijikohara.dbtester.exception.DataSetLoadException
 * @see io.github.seijikohara.dbtester.exception.DataSourceNotFoundException
 * @see io.github.seijikohara.dbtester.exception.ValidationException
 */
package io.github.seijikohara.dbtester.exception;
