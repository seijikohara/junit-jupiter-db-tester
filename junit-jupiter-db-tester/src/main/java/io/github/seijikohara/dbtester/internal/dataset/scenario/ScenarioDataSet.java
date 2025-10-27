package io.github.seijikohara.dbtester.internal.dataset.scenario;

import io.github.seijikohara.dbtester.internal.dataset.DataSet;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;

/**
 * Abstract base class for Collection-based datasets that support scenario filtering.
 *
 * <p>This class provides the foundation for datasets that can filter rows based on scenario names.
 * Scenario filtering allows organizing multiple test scenarios within the same data files by using
 * a special marker column (e.g., {@code #scenario}).
 *
 * <h2>Scenario Filtering</h2>
 *
 * <p>Scenario-based filtering allows multiple test scenarios to coexist in a single data file. A
 * special column (the scenario marker) specifies which rows belong to which test scenario. When
 * loading data with specific scenario names, only matching rows are included.
 *
 * <h2>Collection-Based Design</h2>
 *
 * <p>Unlike DbUnit's array-based API, this implementation uses Java Collections:
 *
 * <ul>
 *   <li>{@link List} for tables (instead of {@code ITable[]})
 *   <li>Immutable collections by default
 *   <li>Framework-independent (no DbUnit dependencies)
 * </ul>
 *
 * <h2>Data Source Association</h2>
 *
 * <p>This class supports associating a JDBC {@link DataSource} with the dataset for multi-database
 * testing scenarios.
 *
 * <h2>Implementation Requirements</h2>
 *
 * <p>Subclasses must implement {@link #getTables()} to provide the scenario-filtered tables.
 *
 * @see DataSet
 * @see ScenarioTable
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioDataSet
 */
public abstract class ScenarioDataSet implements DataSet {

  /** The data source associated with this dataset. */
  private @Nullable DataSource dataSource;

  /** Creates a scenario dataset. */
  protected ScenarioDataSet() {}

  @Override
  public abstract List<Table> getTables();

  @Override
  public Optional<Table> getTable(final TableName tableName) {
    return getTables().stream().filter(table -> table.getName().equals(tableName)).findFirst();
  }

  @Override
  public final Optional<DataSource> getDataSource() {
    return Optional.ofNullable(dataSource);
  }

  /**
   * Sets the data source.
   *
   * <p>This method allows associating a JDBC data source with this dataset for multi-database
   * testing scenarios.
   *
   * @param dataSource the data source, or null to clear the association
   */
  public final void setDataSource(final @Nullable DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
