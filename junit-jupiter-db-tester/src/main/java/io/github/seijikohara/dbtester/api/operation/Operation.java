package io.github.seijikohara.dbtester.api.operation;

/** Standard database operations supported by the extension. */
public enum Operation {

  /** No-op; leaves the database unchanged. */
  NONE,

  /** Updates existing rows without inserting new ones. */
  UPDATE,

  /** Inserts only new rows; fails when duplicates exist. */
  INSERT,

  /** Upserts rows by updating matches and inserting new entries. */
  REFRESH,

  /** Deletes only the dataset rows identified by primary key. */
  DELETE,

  /** Removes all rows from the referenced tables without resetting sequences. */
  DELETE_ALL,

  /** Truncates the tables, resetting identity columns where supported. */
  TRUNCATE_TABLE,

  /** Deletes all rows before inserting the dataset. */
  CLEAN_INSERT,

  /** Truncates the tables before inserting the dataset. */
  TRUNCATE_INSERT
}
