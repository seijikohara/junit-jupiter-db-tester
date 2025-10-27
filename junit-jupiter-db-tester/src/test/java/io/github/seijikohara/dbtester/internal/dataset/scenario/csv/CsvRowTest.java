package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.internal.domain.ColumnName;
import io.github.seijikohara.dbtester.internal.domain.DataValue;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link CsvRow}. */
@DisplayName("CsvRow")
class CsvRowTest {

  /** Constructs test instance. */
  CsvRowTest() {}

  /** Tests for getValues method. */
  @Nested
  @DisplayName("getValues() method")
  class GetValuesMethod {

    /** Constructs test instance. */
    GetValuesMethod() {}

    /** Verifies that all values are returned. */
    @Test
    @Tag("normal")
    @DisplayName("Returns all column values")
    void returnsAllColumnValues() {
      // Given
      final var col1 = new ColumnName("ID");
      final var col2 = new ColumnName("NAME");
      final var values =
          Map.of(
              col1, new DataValue(1),
              col2, new DataValue("Alice"));
      final var row = new CsvRow(values);

      // When
      final var result = row.getValues();

      // Then
      assertEquals(2, result.size());
      assertEquals(new DataValue(1), result.get(col1));
      assertEquals(new DataValue("Alice"), result.get(col2));
    }

    /** Verifies that returned map is immutable. */
    @Test
    @Tag("normal")
    @DisplayName("Returns immutable map")
    void returnsImmutableMap() {
      // Given
      final var col1 = new ColumnName("ID");
      final var values = Map.of(col1, new DataValue(1));
      final var row = new CsvRow(values);

      // When
      final var result = row.getValues();

      // Then
      final var col2 = new ColumnName("NAME");
      assertThrows(
          UnsupportedOperationException.class, () -> result.put(col2, new DataValue("test")));
    }

    /** Verifies that modifications to source map don't affect row. */
    @Test
    @Tag("normal")
    @DisplayName("Defensively copies values map")
    void defensivelyCopiesValuesMap() {
      // Given
      final var col1 = new ColumnName("ID");
      final var col2 = new ColumnName("NAME");
      final var sourceMap = new java.util.HashMap<ColumnName, DataValue>();
      sourceMap.put(col1, new DataValue(1));
      final var row = new CsvRow(sourceMap);

      // When
      sourceMap.put(col2, new DataValue("Alice"));

      // Then
      final var result = row.getValues();
      assertEquals(1, result.size());
      assertFalse(result.containsKey(col2));
    }
  }

  /** Tests for getValue method. */
  @Nested
  @DisplayName("getValue() method")
  class GetValueMethod {

    /** Constructs test instance. */
    GetValueMethod() {}

    /** Verifies that existing column value is returned. */
    @Test
    @Tag("normal")
    @DisplayName("Returns value for existing column")
    void returnsValue_forExistingColumn() {
      // Given
      final var col1 = new ColumnName("ID");
      final var col2 = new ColumnName("NAME");
      final var values =
          Map.of(
              col1, new DataValue(1),
              col2, new DataValue("Alice"));
      final var row = new CsvRow(values);

      // When
      final var result = row.getValue(col1);

      // Then
      assertEquals(new DataValue(1), result);
    }

    /** Verifies that null DataValue is returned for non-existent column. */
    @Test
    @Tag("normal")
    @DisplayName("Returns null DataValue for non-existent column")
    void returnsNullDataValue_forNonExistentColumn() {
      // Given
      final var col1 = new ColumnName("ID");
      final var values = Map.of(col1, new DataValue(1));
      final var row = new CsvRow(values);
      final var nonExistentColumn = new ColumnName("NONEXISTENT");

      // When
      final var result = row.getValue(nonExistentColumn);

      // Then
      assertNotNull(result);
      assertTrue(result.isNull());
    }

    /** Verifies that null value in map is preserved. */
    @Test
    @Tag("edge-case")
    @DisplayName("Returns null DataValue when column contains null")
    void returnsNullDataValue_whenColumnContainsNull() {
      // Given
      final var col1 = new ColumnName("ID");
      final Object nullValue = null;
      final var values = Map.of(col1, new DataValue(nullValue));
      final var row = new CsvRow(values);

      // When
      final var result = row.getValue(col1);

      // Then
      assertNotNull(result);
      assertTrue(result.isNull());
    }
  }
}
