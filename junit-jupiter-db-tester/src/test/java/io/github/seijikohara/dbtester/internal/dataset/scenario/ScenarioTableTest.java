package io.github.seijikohara.dbtester.internal.dataset.scenario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.internal.dataset.Row;
import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ScenarioTable}. */
@DisplayName("ScenarioTable")
class ScenarioTableTest {

  /** Constructs test instance. */
  ScenarioTableTest() {}

  /** Concrete test implementation of ScenarioTable. */
  private static class TestScenarioTable extends ScenarioTable {

    /** Table name. */
    private final TableName name;

    /** Column names. */
    private final List<ColumnName> columns;

    /** Rows. */
    private final List<Row> rows;

    /**
     * Creates a test scenario table.
     *
     * @param name the table name
     * @param columns the column names
     * @param rows the rows
     */
    TestScenarioTable(final TableName name, final List<ColumnName> columns, final List<Row> rows) {
      this.name = name;
      this.columns = columns;
      this.rows = rows;
    }

    @Override
    public TableName getName() {
      return name;
    }

    @Override
    public List<ColumnName> getColumns() {
      return columns;
    }

    @Override
    public List<Row> getRows() {
      return rows;
    }
  }

  /** Tests for getRowCount method. */
  @Nested
  @DisplayName("getRowCount() method")
  class GetRowCountMethod {

    /** Constructs test instance. */
    GetRowCountMethod() {}

    /** Verifies that row count matches number of rows. */
    @Test
    @Tag("normal")
    @DisplayName("Returns correct row count")
    void returnsCorrectRowCount() {
      // Given
      final var tableName = new TableName("USERS");
      final var columns = List.of(new ColumnName("ID"), new ColumnName("NAME"));
      final var row1 = mock(Row.class);
      final var row2 = mock(Row.class);
      final var row3 = mock(Row.class);
      final var rows = List.of(row1, row2, row3);
      final var table = new TestScenarioTable(tableName, columns, rows);

      // When
      final var rowCount = table.getRowCount();

      // Then
      assertEquals(3, rowCount);
    }

    /** Verifies that row count is zero for empty table. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns zero for empty table")
    void returnsZero_forEmptyTable() {
      // Given
      final var tableName = new TableName("EMPTY_TABLE");
      final var columns = List.of(new ColumnName("ID"));
      final var table = new TestScenarioTable(tableName, columns, List.of());

      // When
      final var rowCount = table.getRowCount();

      // Then
      assertEquals(0, rowCount);
    }
  }
}
