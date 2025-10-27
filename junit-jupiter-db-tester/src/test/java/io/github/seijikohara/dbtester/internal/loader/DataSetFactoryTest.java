package io.github.seijikohara.dbtester.internal.loader;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
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

/** Unit tests for {@link DataSetFactory}. */
@DisplayName("DataSetFactory")
class DataSetFactoryTest {

  /** Constructs test instance. */
  DataSetFactoryTest() {}

  /** Tests for createDataSet method. */
  @Nested
  @DisplayName("createDataSet() method")
  class CreateDataSetMethod {

    /** Constructs test instance. */
    CreateDataSetMethod() {}

    /**
     * Verifies that dataset is created from CSV files.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates dataset from CSV files")
    void createsDataSet_fromCsvFiles(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create CSV file with table-ordering.txt
      final var csvFile = tempDir.resolve("TABLE1.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      Files.writeString(orderingFile, "TABLE1\n");

      // When
      final var dataSet = factory.createDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertEquals(1, dataSet.getTables().size());
    }

    /**
     * Verifies that dataset is created with multiple tables.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates dataset with multiple tables")
    void createsDataSet_withMultipleTables(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create multiple CSV files
      Files.writeString(tempDir.resolve("TABLE1.csv"), "ID,NAME\n1,Alice\n");
      Files.writeString(tempDir.resolve("TABLE2.csv"), "ID,VALUE\n1,100\n");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      Files.writeString(orderingFile, "TABLE1\nTABLE2\n");

      // When
      final var dataSet = factory.createDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertEquals(2, dataSet.getTables().size());
    }

    /**
     * Verifies that exception is thrown when directory is empty.
     *
     * @param tempDir temporary directory for test
     */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when no data files found")
    void throwsException_whenNoDataFilesFound(@TempDir final Path tempDir) {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class,
              () -> factory.createDataSet(tempDir, scenarioNames, scenarioMarker));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("No data files found"));
    }

    /** Verifies that exception is thrown when directory doesn't exist. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when directory doesn't exist")
    void throwsException_whenDirectoryDoesNotExist() {
      // Given
      final var factory = new DataSetFactory();
      final var nonExistentDir = Path.of("/nonexistent/directory");
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class,
              () -> factory.createDataSet(nonExistentDir, scenarioNames, scenarioMarker));
      assertNotNull(exception.getMessage());
      assertTrue(
          exception.getMessage().contains("Failed to read directory")
              || exception.getMessage().contains("No data files found"));
    }

    /**
     * Verifies that CSV files are detected among mixed file types.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Detects CSV files among mixed file types")
    void detectsCsvFiles_amongMixedTypes(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create mixed file types
      Files.writeString(tempDir.resolve("README.md"), "# README\n");
      Files.writeString(tempDir.resolve("TABLE1.csv"), "ID,NAME\n1,Alice\n");
      Files.writeString(tempDir.resolve("notes.txt"), "Some notes\n");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      Files.writeString(orderingFile, "TABLE1\n");

      // When
      final var dataSet = factory.createDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertEquals(1, dataSet.getTables().size());
    }

    /**
     * Verifies that dataset is created with empty scenario names.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates dataset with empty scenario names")
    void createsDataSet_withEmptyScenarioNames(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.<ScenarioName>of();
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create CSV file
      final var csvFile = tempDir.resolve("TABLE1.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      Files.writeString(orderingFile, "TABLE1\n");

      // When
      final var dataSet = factory.createDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertEquals(1, dataSet.getTables().size());
    }
  }

  /** Tests for file extension detection. */
  @Nested
  @DisplayName("File extension detection")
  class FileExtensionDetection {

    /** Constructs test instance. */
    FileExtensionDetection() {}

    /**
     * Verifies that table-ordering.txt is not considered a data file.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Ignores table-ordering.txt file")
    void ignoresTableOrderingFile(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create only table-ordering.txt (no data files)
      Files.writeString(tempDir.resolve("table-ordering.txt"), "TABLE1\n");

      // When & Then
      assertThrows(
          DataSetLoadException.class,
          () -> factory.createDataSet(tempDir, scenarioNames, scenarioMarker));
    }

    /**
     * Verifies that extension matching is case-sensitive.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Extension matching is case-sensitive")
    void extensionMatchingIsCaseSensitive(@TempDir final Path tempDir) throws IOException {
      // Given
      final var factory = new DataSetFactory();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create CSV file with uppercase extension (not recognized)
      Files.writeString(tempDir.resolve("TABLE1.CSV"), "ID,NAME\n1,Alice\n");

      // When & Then
      assertThrows(
          DataSetLoadException.class,
          () -> factory.createDataSet(tempDir, scenarioNames, scenarioMarker));
    }
  }
}
