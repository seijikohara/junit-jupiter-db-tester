package io.github.seijikohara.dbtester.internal.dataset.scenario;

import io.github.seijikohara.dbtester.internal.dataset.Row;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;

/**
 * Abstract base class for Collection-based tables that support scenario filtering.
 *
 * <p>This class provides the foundation for tables that can filter rows based on scenario names.
 * Scenario filtering allows organizing multiple test scenarios within the same data files by using
 * a special marker column (e.g., {@code #scenario}).
 *
 * <h2>Scenario Filtering Concept</h2>
 *
 * <p>Scenario-based filtering allows multiple test scenarios to coexist in a single data file. The
 * first column, identified by the scenario marker (see {@link
 * io.github.seijikohara.dbtester.config.ConventionSettings#scenarioMarker()}), specifies which rows
 * belong to which test scenario. When loading data with a specific scenario name, only matching
 * rows are included.
 *
 * <h2>Collection-Based Design</h2>
 *
 * <p>Unlike DbUnit's array-based API, this implementation uses Java Collections:
 *
 * <ul>
 *   <li>{@link List} for rows (instead of indexed access)
 *   <li>{@link java.util.Map} for row values (instead of array indexing)
 *   <li>Immutable collections by default
 *   <li>Framework-independent (no DbUnit dependencies)
 * </ul>
 *
 * <h2>Scenario Marker Column</h2>
 *
 * <p>The scenario marker column is automatically removed from the table metadata and row data. It
 * is used only for filtering and is not part of the logical table structure.
 *
 * <h2>Implementation Requirements</h2>
 *
 * <p>Subclasses must implement:
 *
 * <ul>
 *   <li>{@link #getName()} - return the table name
 *   <li>{@link #getColumns()} - return column names (without the scenario marker)
 *   <li>{@link #getRows()} - return the scenario-filtered rows
 * </ul>
 *
 * @see Table
 * @see ScenarioDataSet
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvScenarioTable
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
