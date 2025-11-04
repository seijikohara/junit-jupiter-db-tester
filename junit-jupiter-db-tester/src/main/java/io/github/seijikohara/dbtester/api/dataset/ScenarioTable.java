package io.github.seijikohara.dbtester.api.dataset;

import io.github.seijikohara.dbtester.api.domain.ColumnName;
import io.github.seijikohara.dbtester.api.domain.TableName;
import java.util.List;

/**
 * Base implementation of {@link Table} that supports removing scenario columns from the payload.
 */
public abstract class ScenarioTable implements Table {

  /**
   * Initializes a new scenario table instance.
   *
   * <p>Subclasses must call this constructor via {@code super()} and then initialize their filtered
   * row collections.
   */
  protected ScenarioTable() {}

  @Override
  public abstract TableName getName();

  @Override
  public abstract List<ColumnName> getColumns();

  @Override
  public abstract List<Row> getRows();

  @Override
  public int getRowCount() {
    return getRows().size();
  }
}
