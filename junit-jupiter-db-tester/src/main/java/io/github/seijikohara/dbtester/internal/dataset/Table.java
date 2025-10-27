package io.github.seijikohara.dbtester.internal.dataset;

import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;

/**
 * Framework-independent table abstraction.
 *
 * <p>Represents a database table with metadata and row data.
 */
public interface Table {

  /**
   * Returns the table name.
   *
   * @return the table name
   */
  TableName getName();

  /**
   * Returns the column names in order.
   *
   * @return immutable list of column names
   */
  List<ColumnName> getColumns();

  /**
   * Returns all rows in this table.
   *
   * @return immutable list of rows
   */
  List<Row> getRows();

  /**
   * Returns the number of rows.
   *
   * @return row count
   */
  int getRowCount();
}
