package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.internal.dataset.Row;
import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.DataValue;
import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link CsvScenarioTable}. */
@DisplayName("CsvScenarioTable")
class CsvScenarioTableTest {

  /** Constructs test instance. */
  CsvScenarioTableTest() {}

  /** Tests for constructor. */
  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    /** Constructs test instance. */
    ConstructorTests() {}

    /** Verifies that constructor creates table without scenario filtering. */
    @Test
    @Tag("normal")
    @DisplayName("Creates table without scenario filtering when scenario column absent")
    void createsTable_withoutScenarioFiltering() {
      // Given
      final var sourceTable = mock(Table.class);
      final var tableName = new TableName("USERS");
      final var columns = List.of(new ColumnName("ID"), new ColumnName("NAME"));
      final var row1 = createMockRow(Map.of("ID", "1", "NAME", "Alice"));
      final var row2 = createMockRow(Map.of("ID", "2", "NAME", "Bob"));

      when(sourceTable.getName()).thenReturn(tableName);
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row1, row2));

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(table);
      assertEquals(tableName, table.getName());
      assertEquals(2, table.getColumns().size());
      assertEquals(2, table.getRows().size());
    }

    /** Verifies that constructor filters rows by scenario. */
    @Test
    @Tag("normal")
    @DisplayName("Filters rows by scenario when scenario column present")
    void filtersRows_byScenario() {
      // Given
      final var sourceTable = mock(Table.class);
      final var tableName = new TableName("USERS");
      final var scenarioCol = new ColumnName("#scenario");
      final var idCol = new ColumnName("ID");
      final var nameCol = new ColumnName("NAME");
      final var columns = List.of(scenarioCol, idCol, nameCol);

      final var row1 = createMockRow(Map.of("#scenario", "test1", "ID", "1", "NAME", "Alice"));
      final var row2 = createMockRow(Map.of("#scenario", "test2", "ID", "2", "NAME", "Bob"));

      when(sourceTable.getName()).thenReturn(tableName);
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row1, row2));

      final var scenarioNames = List.of(new ScenarioName("test1"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(table);
      assertEquals(2, table.getColumns().size());
      assertEquals(1, table.getRows().size());
    }

    /** Verifies that constructor excludes scenario column from result. */
    @Test
    @Tag("normal")
    @DisplayName("Excludes scenario column from resulting columns")
    void excludesScenarioColumn_fromResult() {
      // Given
      final var sourceTable = mock(Table.class);
      final var tableName = new TableName("USERS");
      final var scenarioCol = new ColumnName("#scenario");
      final var idCol = new ColumnName("ID");
      final var columns = List.of(scenarioCol, idCol);

      final var row = createMockRow(Map.of("#scenario", "test1", "ID", "1"));

      when(sourceTable.getName()).thenReturn(tableName);
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row));

      final var scenarioNames = List.of(new ScenarioName("test1"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // Then
      assertEquals(1, table.getColumns().size());
      assertEquals(idCol, table.getColumns().get(0));
    }
  }

  /** Tests for getName method. */
  @Nested
  @DisplayName("getName() method")
  class GetNameMethod {

    /** Constructs test instance. */
    GetNameMethod() {}

    /** Verifies that getName returns table name. */
    @Test
    @Tag("normal")
    @DisplayName("Returns table name from source table")
    void returnsTableName() {
      // Given
      final var sourceTable = mock(Table.class);
      final var tableName = new TableName("USERS");
      when(sourceTable.getName()).thenReturn(tableName);
      when(sourceTable.getColumns()).thenReturn(List.of(new ColumnName("ID")));
      when(sourceTable.getRows()).thenReturn(List.of());

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // When
      final var result = table.getName();

      // Then
      assertEquals(tableName, result);
    }
  }

  /** Tests for getColumns method. */
  @Nested
  @DisplayName("getColumns() method")
  class GetColumnsMethod {

    /** Constructs test instance. */
    GetColumnsMethod() {}

    /** Verifies that getColumns returns immutable list. */
    @Test
    @Tag("normal")
    @DisplayName("Returns immutable list of columns")
    void returnsImmutableList() {
      // Given
      final var sourceTable = mock(Table.class);
      final var columns = List.of(new ColumnName("ID"), new ColumnName("NAME"));
      when(sourceTable.getName()).thenReturn(new TableName("USERS"));
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of());

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // When
      final var result = table.getColumns();

      // Then
      assertNotNull(result);
      assertThrows(UnsupportedOperationException.class, () -> result.add(new ColumnName("TEST")));
    }
  }

  /** Tests for getRows method. */
  @Nested
  @DisplayName("getRows() method")
  class GetRowsMethod {

    /** Constructs test instance. */
    GetRowsMethod() {}

    /** Verifies that getRows returns immutable list. */
    @Test
    @Tag("normal")
    @DisplayName("Returns immutable list of rows")
    void returnsImmutableList() {
      // Given
      final var sourceTable = mock(Table.class);
      final var columns = List.of(new ColumnName("ID"));
      final var row = createMockRow(Map.of("ID", "1"));

      when(sourceTable.getName()).thenReturn(new TableName("USERS"));
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row));

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // When
      final var result = table.getRows();

      // Then
      assertNotNull(result);
      assertThrows(UnsupportedOperationException.class, () -> result.add(mock(Row.class)));
    }

    /** Verifies that empty string values are normalized to null. */
    @Test
    @Tag("normal")
    @DisplayName("Normalizes empty strings to null values")
    void normalizesEmptyStrings_toNull() {
      // Given
      final var sourceTable = mock(Table.class);
      final var columns = List.of(new ColumnName("ID"), new ColumnName("NAME"));
      final var row = createMockRow(Map.of("ID", "1", "NAME", ""));

      when(sourceTable.getName()).thenReturn(new TableName("USERS"));
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row));

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // When
      final var result = table.getRows();

      // Then
      assertEquals(1, result.size());
      final var resultRow = result.get(0);
      final var nameValue = resultRow.getValue(new ColumnName("NAME"));
      assertNull(nameValue.value());
    }
  }

  /** Tests for getRowCount method. */
  @Nested
  @DisplayName("getRowCount() method")
  class GetRowCountMethod {

    /** Constructs test instance. */
    GetRowCountMethod() {}

    /** Verifies that getRowCount returns correct count. */
    @Test
    @Tag("normal")
    @DisplayName("Returns correct row count")
    void returnsCorrectRowCount() {
      // Given
      final var sourceTable = mock(Table.class);
      final var columns = List.of(new ColumnName("ID"));
      final var row1 = createMockRow(Map.of("ID", "1"));
      final var row2 = createMockRow(Map.of("ID", "2"));

      when(sourceTable.getName()).thenReturn(new TableName("USERS"));
      when(sourceTable.getColumns()).thenReturn(columns);
      when(sourceTable.getRows()).thenReturn(List.of(row1, row2));

      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var table = new CsvScenarioTable(sourceTable, scenarioNames, scenarioMarker);

      // When
      final var count = table.getRowCount();

      // Then
      assertEquals(2, count);
    }
  }

  /**
   * Creates a mock row with specified column values.
   *
   * @param columnValues map of column names to values
   * @return mocked Row instance
   */
  private Row createMockRow(final Map<String, String> columnValues) {
    final var row = mock(Row.class);
    columnValues.forEach(
        (colName, value) -> {
          final var columnName = new ColumnName(colName);
          final var dataValue = new DataValue(value);
          when(row.getValue(columnName)).thenReturn(dataValue);
        });
    return row;
  }
}
