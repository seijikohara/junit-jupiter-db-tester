package io.github.seijikohara.dbtester.api.dataset;

import io.github.seijikohara.dbtester.api.domain.ColumnName;
import io.github.seijikohara.dbtester.api.domain.DataValue;
import java.util.Map;

/** Represents a single logical row within a {@link Table}. */
public interface Row {

  /**
   * Returns the column/value pairs that compose this row.
   *
   * @return immutable mapping of columns to their values
   */
  Map<ColumnName, DataValue> getValues();

  /**
   * Resolves the value associated with {@code column}. If the column is absent the method returns a
   * {@link DataValue} encapsulating {@code null}.
   *
   * @param column identifier of the column to look up
   * @return data value for the requested column, wrapping {@code null} when absent
   */
  DataValue getValue(ColumnName column);
}
