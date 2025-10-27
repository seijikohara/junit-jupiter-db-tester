package io.github.seijikohara.dbtester.internal.dataset.scenario.csv;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.internal.dataset.scenario.ScenarioDataSet;
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

/** Unit tests for {@link CsvFormatProvider}. */
@DisplayName("CsvFormatProvider")
class CsvFormatProviderTest {

  /** Constructs test instance. */
  CsvFormatProviderTest() {}

  /** Tests for supportedExtension method. */
  @Nested
  @DisplayName("supportedExtension() method")
  class SupportedExtensionMethod {

    /** Constructs test instance. */
    SupportedExtensionMethod() {}

    /** Verifies that CSV extension is returned. */
    @Test
    @Tag("normal")
    @DisplayName("Returns .csv extension")
    void returnsCsvExtension() {
      // Given
      final var provider = new CsvFormatProvider();

      // When
      final var extension = provider.supportedExtension();

      // Then
      assertEquals(".csv", extension);
    }
  }

  /** Tests for createDataSet method. */
  @Nested
  @DisplayName("createDataSet() method")
  class CreateDataSetMethod {

    /** Constructs test instance. */
    CreateDataSetMethod() {}

    /**
     * Verifies that CSV dataset is created.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates CsvScenarioDataSet instance")
    void createsCsvScenarioDataSet(@TempDir final Path tempDir) throws IOException {
      // Given
      final var provider = new CsvFormatProvider();
      final var scenarioNames = List.of(new ScenarioName("test"));
      final var scenarioMarker = new ScenarioMarker("#scenario");

      // Create minimal CSV files for testing
      final var csvFile = tempDir.resolve("TEST.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Alice\n");

      // When
      final var dataSet = provider.createDataSet(tempDir, scenarioNames, scenarioMarker);

      // Then
      assertNotNull(dataSet);
      assertInstanceOf(ScenarioDataSet.class, dataSet);
      assertInstanceOf(CsvScenarioDataSet.class, dataSet);
    }
  }
}
