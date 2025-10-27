package io.github.seijikohara.dbtester.internal.bridge.dbunit.adapter;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import io.github.seijikohara.dbtester.internal.dataset.DataSet;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

/**
 * Adapter that converts DbUnit's IDataSet to our DataSet interface.
 *
 * <p>This adapter bridges between DbUnit's array-based API and our Collection-based abstraction,
 * allowing the rest of the codebase to work with immutable collections instead of arrays.
 */
public final class DbUnitDataSetAdapter implements DataSet {

  /** The DbUnit dataset being adapted. */
  private final IDataSet delegate;

  /**
   * Creates a new adapter.
   *
   * @param delegate the DbUnit dataset to adapt
   */
  public DbUnitDataSetAdapter(final IDataSet delegate) {
    this.delegate = delegate;
  }

  @Override
  public List<Table> getTables() {
    try {
      final var tableNames = delegate.getTableNames();
      return Stream.of(tableNames).map(this::getTableByName).toList();
    } catch (final DataSetException e) {
      throw new DataSetLoadException("Failed to load tables", e);
    }
  }

  @Override
  public Optional<Table> getTable(final TableName tableName) {
    try {
      final var table = delegate.getTable(tableName.value());
      return Optional.of(new DbUnitTableAdapter(table));
    } catch (final DataSetException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<DataSource> getDataSource() {
    return Optional.empty();
  }

  /**
   * Gets a table by name.
   *
   * @param name the table name
   * @return the adapted table
   */
  private Table getTableByName(final String name) {
    try {
      return new DbUnitTableAdapter(delegate.getTable(name));
    } catch (final DataSetException e) {
      throw new DataSetLoadException(String.format("Failed to load table: %s", name), e);
    }
  }
}
