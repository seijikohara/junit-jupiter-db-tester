package io.github.seijikohara.dbtester.api.operation;

/**
 * Database operations for manipulating test data.
 *
 * <p>This enumeration defines the standard operations available for preparing database state during
 * test execution. Each operation corresponds to a specific data manipulation strategy, such as
 * inserting, updating, deleting, or truncating tables.
 *
 * <h2>Recommended Operations</h2>
 *
 * <ul>
 *   <li>{@link #CLEAN_INSERT} - Most common choice; deletes existing data then inserts new data
 *   <li>{@link #REFRESH} - Updates existing rows and inserts new ones (upsert)
 *   <li>{@link #TRUNCATE_INSERT} - Truncates tables then inserts; useful when resetting
 *       auto-increment sequences
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.api.annotation.Preparation#operation()
 */
public enum Operation {

  /**
   * Performs no operation.
   *
   * <p>The dataset is loaded but no database modifications are made. This is useful for loading
   * metadata or when the dataset is used only for validation purposes.
   */
  NONE,

  /**
   * Updates existing rows in the database.
   *
   * <p>This operation updates rows that already exist in the database. If a row specified in the
   * dataset does not exist, the operation fails. Use this when you need to modify existing data
   * without inserting new rows.
   */
  UPDATE,

  /**
   * Inserts new rows into the database.
   *
   * <p>This operation inserts rows from the dataset. If a row with the same primary key already
   * exists, the operation fails. Use this when you need to add new data without modifying existing
   * rows.
   */
  INSERT,

  /**
   * Refreshes the database by updating existing rows and inserting new ones.
   *
   * <p>This operation performs an "upsert": it updates rows that already exist and inserts rows
   * that do not. This is useful for incrementally building up test data or updating specific rows
   * while preserving others.
   */
  REFRESH,

  /**
   * Deletes specific rows from the database.
   *
   * <p>This operation deletes only the rows specified in the dataset, identified by their primary
   * keys. Other rows in the tables are preserved.
   */
  DELETE,

  /**
   * Deletes all rows from the specified tables.
   *
   * <p>This operation removes all data from the tables referenced in the dataset, but does not
   * reset auto-increment sequences. The table structure remains intact.
   */
  DELETE_ALL,

  /**
   * Truncates the specified tables.
   *
   * <p>This operation removes all data from the tables and resets auto-increment sequences to their
   * initial values. This is faster than {@link #DELETE_ALL} but may not be supported by all
   * databases and typically cannot be rolled back.
   */
  TRUNCATE_TABLE,

  /**
   * Deletes all existing data from the tables, then inserts the dataset.
   *
   * <p>This is the recommended operation for most tests. It ensures a clean, predictable state by
   * removing all existing data before inserting the test data. Auto-increment sequences are not
   * reset.
   *
   * @see #TRUNCATE_INSERT
   */
  CLEAN_INSERT,

  /**
   * Truncates the tables, then inserts the dataset.
   *
   * <p>This operation is similar to {@link #CLEAN_INSERT}, but uses {@code TRUNCATE} instead of
   * {@code DELETE}. Truncating resets auto-increment sequences to their initial values, making
   * generated IDs predictable. This is useful when tests depend on specific ID values or require
   * consistent sequence numbers.
   *
   * <p><strong>Important:</strong> Truncate operations cannot be rolled back in some database
   * systems and may not be supported by all databases. Use this operation only when sequence
   * resetting is necessary.
   *
   * @see #CLEAN_INSERT
   * @see #TRUNCATE_TABLE
   */
  TRUNCATE_INSERT
}
