package io.github.seijikohara.dbtester.api.dataset;

import io.github.seijikohara.dbtester.api.domain.ColumnName;
import io.github.seijikohara.dbtester.api.domain.TableName;
import java.util.List;

/** Describes the structure and rows of a single database table. */
public interface Table {

  /**
   * Returns the logical name of the table.
   *
   * @return table identifier
   */
  TableName getName();

  /**
   * Returns the column names in the order expected by the dataset.
   *
   * @return ordered collection of column identifiers
   */
  List<ColumnName> getColumns();

  /**
   * Returns the rows that constitute this table.
   *
   * @return immutable list of rows
   */
  List<Row> getRows();

  /**
   * Returns the row count (equivalent to {@code getRows().size()}).
   *
   * @return number of rows contained in the table
   */
  int getRowCount();
}
