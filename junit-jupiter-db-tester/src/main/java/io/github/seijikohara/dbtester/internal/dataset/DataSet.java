package io.github.seijikohara.dbtester.internal.dataset;

import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Framework-independent dataset abstraction.
 *
 * <p>Represents a collection of database tables with their data. This interface uses
 * Collection-based APIs instead of array-based APIs.
 */
public interface DataSet {

  /**
   * Returns all tables in this dataset.
   *
   * @return immutable list of tables
   */
  List<Table> getTables();

  /**
   * Returns the table with the specified name.
   *
   * @param tableName the table name
   * @return the table, or empty if not found
   */
  Optional<Table> getTable(TableName tableName);

  /**
   * Returns the associated data source.
   *
   * @return the data source, or empty if not associated
   */
  Optional<DataSource> getDataSource();
}
