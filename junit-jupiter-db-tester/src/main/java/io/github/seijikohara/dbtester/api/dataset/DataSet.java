package io.github.seijikohara.dbtester.api.dataset;

import io.github.seijikohara.dbtester.api.domain.TableName;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/** Represents a logical dataset comprised of one or more tables and an optional data source. */
public interface DataSet {

  /**
   * Returns the tables that belong to this dataset in declaration order.
   *
   * @return immutable list of tables composing the dataset
   */
  List<Table> getTables();

  /**
   * Resolves a table by name.
   *
   * @param tableName logical table identifier
   * @return the matching table, or an empty optional when the dataset does not contain that table
   */
  Optional<Table> getTable(TableName tableName);

  /**
   * Returns the data source that should be used when executing the dataset.
   *
   * @return optional containing the bound data source when present
   */
  Optional<DataSource> getDataSource();
}
