package io.github.seijikohara.dbtester.internal.dataset.scenario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.internal.dataset.Table;
import io.github.seijikohara.dbtester.internal.domain.TableName;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ScenarioDataSet}. */
@DisplayName("ScenarioDataSet")
class ScenarioDataSetTest {

  /** Constructs test instance. */
  ScenarioDataSetTest() {}

  /** Concrete test implementation of ScenarioDataSet. */
  private static class TestScenarioDataSet extends ScenarioDataSet {

    /** Tables in this dataset. */
    private final List<Table> tables;

    /**
     * Creates a test scenario dataset.
     *
     * @param tables the tables
     */
    TestScenarioDataSet(final List<Table> tables) {
      this.tables = tables;
    }

    @Override
    public List<Table> getTables() {
      return tables;
    }
  }

  /** Tests for getTable method. */
  @Nested
  @DisplayName("getTable() method")
  class GetTableMethod {

    /** Test tables. */
    private Table usersTable;

    /** Test tables. */
    private Table ordersTable;

    /** Test dataset. */
    private ScenarioDataSet dataset;

    /** Constructs test instance. */
    GetTableMethod() {}

    /** Sets up test fixtures before each test. */
    @BeforeEach
    void setUp() {
      usersTable = mock(Table.class);
      when(usersTable.getName()).thenReturn(new TableName("USERS"));

      ordersTable = mock(Table.class);
      when(ordersTable.getName()).thenReturn(new TableName("ORDERS"));

      dataset = new TestScenarioDataSet(List.of(usersTable, ordersTable));
    }

    /** Verifies that table is returned when it exists. */
    @Test
    @Tag("normal")
    @DisplayName("Returns table when it exists")
    void returnsTable_whenItExists() {
      // When
      final var result = dataset.getTable(new TableName("USERS"));

      // Then
      assertTrue(result.isPresent());
      result.ifPresent(table -> assertEquals(usersTable, table));
    }

    /** Verifies that empty is returned when table doesn't exist. */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty when table doesn't exist")
    void returnsEmpty_whenTableDoesNotExist() {
      // When
      final var result = dataset.getTable(new TableName("NONEXISTENT"));

      // Then
      assertTrue(result.isEmpty());
    }
  }

  /** Tests for getDataSource and setDataSource methods. */
  @Nested
  @DisplayName("getDataSource() and setDataSource() methods")
  class DataSourceMethods {

    /** Test dataset. */
    private ScenarioDataSet dataset;

    /** Constructs test instance. */
    DataSourceMethods() {}

    /** Sets up test fixtures before each test. */
    @BeforeEach
    void setUp() {
      dataset = new TestScenarioDataSet(List.of());
    }

    /** Verifies that data source is empty by default. */
    @Test
    @Tag("normal")
    @DisplayName("Returns empty data source by default")
    void returnsEmpty_byDefault() {
      // When
      final var result = dataset.getDataSource();

      // Then
      assertTrue(result.isEmpty());
    }

    /** Verifies that data source can be set and retrieved. */
    @Test
    @Tag("normal")
    @DisplayName("Sets and retrieves data source")
    void setsAndRetrievesDataSource() {
      // Given
      final var dataSource = mock(DataSource.class);

      // When
      dataset.setDataSource(dataSource);

      // Then
      final var result = dataset.getDataSource();
      assertTrue(result.isPresent());
      result.ifPresent(ds -> assertEquals(dataSource, ds));
    }

    /** Verifies that data source can be cleared. */
    @Test
    @Tag("normal")
    @DisplayName("Clears data source when set to null")
    void clearsDataSource_whenSetToNull() {
      // Given
      final var dataSource = mock(DataSource.class);
      dataset.setDataSource(dataSource);

      // When
      final DataSource nullDataSource = null;
      dataset.setDataSource(nullDataSource);

      // Then
      final var result = dataset.getDataSource();
      assertTrue(result.isEmpty());
    }
  }
}
