package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.internal.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.internal.domain.ScenarioName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for {@link CsvScenarioDataSet}. */
@DisplayName("CsvScenarioDataSet")
class CsvScenarioDataSetTest {

  /** Constructs test instance. */
  CsvScenarioDataSetTest() {}

  /** Tests for constructor without scenario filtering. */
  @Nested
  @DisplayName("CsvScenarioDataSet(Path, ScenarioMarker) constructor")
  class ConstructorWithoutScenarios {

    /** Constructs test instance. */
    ConstructorWithoutScenarios() {}

    /**
     * Verifies that dataset loads all rows when no scenario filtering is applied.
     *
     * @param tempDir temporary directory for test files
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Loads all rows without scenario filtering")
    void loadsAllRows_withoutScenarioFiltering(@TempDir final Path tempDir) throws IOException {
      // Given
      final var csvFile = tempDir.resolve("USERS.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n2,Bob\n");
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertNotNull(dataSet.getTables());
      assertFalse(dataSet.getTables().isEmpty());
    }

    /**
     * Verifies that empty directory creates empty dataset.
     *
     * @param tempDir temporary directory for test files
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Creates empty dataset for empty directory")
    void createsEmptyDataSet_forEmptyDirectory(@TempDir final Path tempDir) {
      // Given
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertNotNull(dataSet.getTables());
      assertTrue(dataSet.getTables().isEmpty());
    }
  }

  /** Tests for constructor with scenario filtering. */
  @Nested
  @DisplayName("CsvScenarioDataSet(Path, Collection, ScenarioMarker) constructor")
  class ConstructorWithScenarios {

    /** Constructs test instance. */
    ConstructorWithScenarios() {}

    /**
     * Verifies that dataset filters rows by scenario.
     *
     * @param tempDir temporary directory for test files
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Filters rows by scenario name")
    void filtersRowsByScenario(@TempDir final Path tempDir) throws IOException {
      // Given
      final var csvFile = tempDir.resolve("USERS.csv");
      Files.writeString(csvFile, "#scenario,ID,NAME\ntest1,1,Alice\ntest2,2,Bob\n");
      final var scenarioNames = List.of(new ScenarioName("test1"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertNotNull(dataSet.getTables());
      assertFalse(dataSet.getTables().isEmpty());
    }

    /**
     * Verifies that empty scenario list loads all rows.
     *
     * @param tempDir temporary directory for test files
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Loads all rows when scenario list is empty")
    void loadsAllRows_whenScenarioListIsEmpty(@TempDir final Path tempDir) throws IOException {
      // Given
      final var csvFile = tempDir.resolve("USERS.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n2,Bob\n");
      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertNotNull(dataSet.getTables());
      assertFalse(dataSet.getTables().isEmpty());
    }
  }

  /** Tests for getTables method. */
  @Nested
  @DisplayName("getTables() method")
  class GetTablesMethod {

    /** Constructs test instance. */
    GetTablesMethod() {}

    /**
     * Verifies that getTables returns immutable list.
     *
     * @param tempDir temporary directory for test files
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns immutable list of tables")
    void returnsImmutableList(@TempDir final Path tempDir) throws IOException {
      // Given
      final var csvFile = tempDir.resolve("USERS.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n");
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioMarker);

      // When
      final var tables = dataSet.getTables();

      // Then
      assertNotNull(tables);
      assertThrows(UnsupportedOperationException.class, () -> tables.add(null));
    }

    /**
     * Verifies that getTables returns consistent results.
     *
     * @param tempDir temporary directory for test files
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Returns consistent results on multiple calls")
    void returnsConsistentResults(@TempDir final Path tempDir) throws IOException {
      // Given
      final var csvFile = tempDir.resolve("USERS.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n");
      final var scenarioMarker = new ScenarioMarker("#scenario");
      final var dataSet = new CsvScenarioDataSet(tempDir, scenarioMarker);

      // When
      final var tables1 = dataSet.getTables();
      final var tables2 = dataSet.getTables();

      // Then
      assertSame(tables1, tables2);
    }
  }
}
