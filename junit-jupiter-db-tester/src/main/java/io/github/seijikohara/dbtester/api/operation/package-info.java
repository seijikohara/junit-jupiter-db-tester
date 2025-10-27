/**
 * Database operation definitions for test data manipulation.
 *
 * <h2>Overview</h2>
 *
 * <p>This package provides type-safe database operation enumerations for controlling how test data
 * is loaded into the database. Operations wrap DbUnit's {@link
 * org.dbunit.operation.DatabaseOperation} with clear semantics and documentation.
 *
 * <h2>Core Enum</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.api.operation.Operation} - Database operation types
 * </ul>
 *
 * <h2>Available Operations</h2>
 *
 * <h3>CLEAN_INSERT (Default)</h3>
 *
 * <p>Deletes all existing data from target tables, then inserts test data. This is the framework
 * default, providing clean state with full transactional compatibility.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.CLEAN_INSERT)
 * }</pre>
 *
 * <h3>TRUNCATE_INSERT</h3>
 *
 * <p>Truncates tables (faster than DELETE_ALL) and resets auto-increment sequences, then inserts
 * test data. Use when test assertions depend on specific ID values and transactional rollback is
 * not required.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.TRUNCATE_INSERT)
 * void testWithPredictableIds() {
 *   // IDs will start from 1
 * }
 * }</pre>
 *
 * <h3>INSERT</h3>
 *
 * <p>Inserts new rows. Fails if rows with the same primary key already exist. Use for additive
 * tests that build on existing data.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.INSERT)
 * void testAdditionalData() {
 *   // Adds to existing database state
 * }
 * }</pre>
 *
 * <h3>REFRESH (Upsert)</h3>
 *
 * <p>Updates existing rows or inserts new ones. Useful for test scenarios that modify existing data
 * without full table replacement.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.REFRESH)
 * void testDataUpdate() {
 *   // Updates existing rows, inserts new ones
 * }
 * }</pre>
 *
 * <h3>UPDATE</h3>
 *
 * <p>Updates existing rows only. Fails if rows don't exist. Use for tests that modify specific
 * records.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.UPDATE)
 * void testModifyExistingData() {
 *   // Updates must match existing primary keys
 * }
 * }</pre>
 *
 * <h3>DELETE</h3>
 *
 * <p>Deletes specified rows based on primary key. Use for testing deletion scenarios.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.DELETE)
 * void testAfterDeletion() {
 *   // Removes specific rows from database
 * }
 * }</pre>
 *
 * <h3>DELETE_ALL</h3>
 *
 * <p>Deletes all rows from target tables. Similar to CLEAN_INSERT but without the insert step.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.DELETE_ALL)
 * void testEmptyDatabase() {
 *   // Clears all data from tables
 * }
 * }</pre>
 *
 * <h3>TRUNCATE_TABLE</h3>
 *
 * <p>Truncates tables and resets auto-increment sequences. Faster than DELETE_ALL but cannot be
 * rolled back in some databases.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.TRUNCATE_TABLE)
 * void testCleanState() {
 *   // Fast table clearing with sequence reset
 * }
 * }</pre>
 *
 * <h3>NONE</h3>
 *
 * <p>No operation performed. Use when you want to specify data location but not load it, or for
 * expectation-only tests.
 *
 * <pre>{@code
 * @Preparation(operation = Operation.NONE)
 * void testWithoutDataLoading() {
 *   // No data loaded, test uses existing database state
 * }
 * }</pre>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Default Operation (Class Level)</h3>
 *
 * <pre>{@code
 * @Preparation(operation = Operation.CLEAN_INSERT)
 * class UserServiceTest {
 *   // All tests use CLEAN_INSERT by default
 * }
 * }</pre>
 *
 * <h3>Per-DataSet Operation</h3>
 *
 * <pre>{@code
 * @Preparation(dataSets = {
 *     @DataSet(operation = Operation.CLEAN_INSERT),    // Primary data
 *     @DataSet(operation = Operation.INSERT,           // Additional data
 *              resourceLocation = "classpath:extra/")
 * })
 * }</pre>
 *
 * <h3>Mixed Operations</h3>
 *
 * <pre>{@code
 * @Preparation(
 *     dataSets = {
 *         @DataSet(dataSourceName = "primary",
 *                  operation = Operation.CLEAN_INSERT),
 *         @DataSet(dataSourceName = "cache",
 *                  operation = Operation.TRUNCATE_INSERT)
 *     }
 * )
 * void testMultipleDatabases() {
 *   // Different operations for different data sources
 * }
 * }</pre>
 *
 * <h2>Operation Characteristics</h2>
 *
 * <table border="1">
 *   <caption>Database Operation Comparison</caption>
 *   <tr>
 *     <th>Operation</th>
 *     <th>Clears Existing</th>
 *     <th>Resets Sequences</th>
 *     <th>Transactional</th>
 *     <th>Use Case</th>
 *   </tr>
 *   <tr>
 *     <td>CLEAN_INSERT</td>
 *     <td>Yes</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Standard tests</td>
 *   </tr>
 *   <tr>
 *     <td>TRUNCATE_INSERT</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Partial</td>
 *     <td>ID-dependent tests</td>
 *   </tr>
 *   <tr>
 *     <td>INSERT</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Additive tests</td>
 *   </tr>
 *   <tr>
 *     <td>REFRESH</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Update scenarios</td>
 *   </tr>
 *   <tr>
 *     <td>UPDATE</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Modify existing</td>
 *   </tr>
 *   <tr>
 *     <td>DELETE</td>
 *     <td>Partial</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Remove specific rows</td>
 *   </tr>
 *   <tr>
 *     <td>DELETE_ALL</td>
 *     <td>Yes</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Clear tables</td>
 *   </tr>
 *   <tr>
 *     <td>TRUNCATE_TABLE</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Partial</td>
 *     <td>Fast clearing</td>
 *   </tr>
 *   <tr>
 *     <td>NONE</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>N/A</td>
 *     <td>No data loading</td>
 *   </tr>
 * </table>
 *
 * <h2>Best Practices</h2>
 *
 * <h3>Use CLEAN_INSERT by Default</h3>
 *
 * <p>Use CLEAN_INSERT (the framework default) for most tests to ensure predictable, isolated
 * database state with full transactional compatibility.
 *
 * <h3>Use TRUNCATE_INSERT for ID-Dependent Tests</h3>
 *
 * <p>When test assertions check specific ID values, use TRUNCATE_INSERT to reset auto-increment
 * sequences. Note that TRUNCATE operations may not be rollbackable in transactional tests on some
 * databases.
 *
 * <h3>Use INSERT for Additive Tests</h3>
 *
 * <p>When tests build upon existing data rather than requiring a clean slate, use INSERT to add
 * data without clearing existing rows.
 *
 * <h3>Document Non-Standard Operations</h3>
 *
 * <p>When using operations other than CLEAN_INSERT, document why:
 *
 * <pre>{@code
 * // Use REFRESH to update existing reference data without full replacement
 * @Preparation(operation = Operation.REFRESH)
 * void testIncrementalUpdate() {
 *   // ...
 * }
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>All operations are thread-safe as they are stateless enums. Database-level locking depends on
 * the underlying database and transaction isolation level.
 *
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation
 * @see io.github.seijikohara.dbtester.api.annotation.DataSet
 * @see org.dbunit.operation.DatabaseOperation
 */
package io.github.seijikohara.dbtester.api.operation;
