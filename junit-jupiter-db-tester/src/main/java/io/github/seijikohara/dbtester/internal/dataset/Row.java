package io.github.seijikohara.dbtester.internal.dataset;

import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.DataValue;
import java.util.Map;

/**
 * Framework-independent row abstraction.
 *
 * <p>Represents a single row in a table as a map of column names to values.
 */
public interface Row {

  /**
   * Returns all column values.
   *
   * @return immutable map of column names to values
   */
  Map<ColumnName, DataValue> getValues();

  /**
   * Returns the value for the specified column.
   *
   * <p>Since {@link DataValue} can represent NULL database values, this method always returns a
   * {@link DataValue}. If the column does not exist in the row or contains a NULL value, a {@link
   * DataValue} wrapping null is returned.
   *
   * @param column the column name
   * @return the value (never null, but may wrap a null value)
   */
  DataValue getValue(ColumnName column);
}
