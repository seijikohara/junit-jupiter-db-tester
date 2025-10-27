package io.github.seijikohara.dbtester.internal.dataset;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for {@link TableOrderingResolver}. */
@DisplayName("TableOrderingResolver")
class TableOrderingResolverTest {

  /** Constructs test instance. */
  TableOrderingResolverTest() {}

  /** Concrete test implementation of TableOrderingResolver. */
  private static class TestTableOrderingResolver extends TableOrderingResolver {

    /** File extension for test files. */
    private final String extension;

    /**
     * Creates a test resolver with the specified extension.
     *
     * @param extension the file extension
     */
    TestTableOrderingResolver(final String extension) {
      this.extension = extension;
    }

    @Override
    protected String getFileExtension() {
      return extension;
    }
  }

  /** Tests for ensureTableOrdering method when table-ordering.txt exists. */
  @Nested
  @DisplayName("ensureTableOrdering() with existing file")
  class EnsureTableOrderingWithExistingFile {

    /** Constructs test instance. */
    EnsureTableOrderingWithExistingFile() {}

    /**
     * Verifies that existing table-ordering.txt is preserved.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Preserves existing table-ordering.txt")
    void preservesExistingFile(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      final var existingContent = List.of("CUSTOM_TABLE", "ANOTHER_TABLE");
      Files.write(orderingFile, existingContent);

      // When
      final var result = resolver.ensureTableOrdering(tempDir);

      // Then
      assertEquals(tempDir, result);
      assertTrue(Files.exists(orderingFile));
      final var actualContent = Files.readAllLines(orderingFile);
      assertEquals(existingContent, actualContent);
    }
  }

  /** Tests for ensureTableOrdering method when table-ordering.txt doesn't exist. */
  @Nested
  @DisplayName("ensureTableOrdering() without existing file")
  class EnsureTableOrderingWithoutExistingFile {

    /** Constructs test instance. */
    EnsureTableOrderingWithoutExistingFile() {}

    /**
     * Verifies that table-ordering.txt is created with alphabetically sorted tables.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Creates table-ordering.txt with sorted table names")
    void createsFileWithSortedTables(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");
      Files.createFile(tempDir.resolve("USERS.csv"));
      Files.createFile(tempDir.resolve("ORDERS.csv"));
      Files.createFile(tempDir.resolve("AUDIT_LOG.csv"));

      // When
      final var result = resolver.ensureTableOrdering(tempDir);

      // Then
      assertEquals(tempDir, result);
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      assertTrue(Files.exists(orderingFile));
      final var tableNames = Files.readAllLines(orderingFile);
      assertEquals(List.of("AUDIT_LOG", "ORDERS", "USERS"), tableNames);
    }

    /**
     * Verifies that only files with matching extension are included.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Filters files by extension")
    void filtersFilesByExtension(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");
      Files.createFile(tempDir.resolve("TABLE1.csv"));
      Files.createFile(tempDir.resolve("TABLE2.txt"));
      Files.createFile(tempDir.resolve("TABLE3.csv"));
      Files.createFile(tempDir.resolve("README.md"));

      // When
      resolver.ensureTableOrdering(tempDir);

      // Then
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      final var tableNames = Files.readAllLines(orderingFile);
      assertEquals(List.of("TABLE1", "TABLE3"), tableNames);
    }

    /**
     * Verifies that extension matching is case-insensitive.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Handles case-insensitive extension matching")
    void handlesCaseInsensitiveExtension(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");
      Files.createFile(tempDir.resolve("TABLE1.csv"));
      Files.createFile(tempDir.resolve("TABLE2.CSV"));
      Files.createFile(tempDir.resolve("TABLE3.Csv"));

      // When
      resolver.ensureTableOrdering(tempDir);

      // Then
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      final var tableNames = Files.readAllLines(orderingFile);
      assertEquals(3, tableNames.size());
      assertTrue(tableNames.contains("TABLE1"));
      assertTrue(tableNames.contains("TABLE2"));
      assertTrue(tableNames.contains("TABLE3"));
    }

    /**
     * Verifies that empty directory creates empty table-ordering.txt.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Creates empty table-ordering.txt for empty directory")
    void createsEmptyFileForEmptyDirectory(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");

      // When
      resolver.ensureTableOrdering(tempDir);

      // Then
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      assertTrue(Files.exists(orderingFile));
      final var tableNames = Files.readAllLines(orderingFile);
      assertTrue(tableNames.isEmpty());
    }

    /**
     * Verifies that table-ordering.txt file itself is not included.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("edge-case")
    @DisplayName("Excludes table-ordering.txt from table list")
    void excludesOrderingFileFromList(@TempDir final Path tempDir) throws IOException {
      // Given
      final var resolver = new TestTableOrderingResolver(".txt");
      Files.createFile(tempDir.resolve("TABLE1.txt"));

      // When
      resolver.ensureTableOrdering(tempDir);

      // Then
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      final var tableNames = Files.readAllLines(orderingFile);
      assertEquals(List.of("TABLE1"), tableNames);
      assertFalse(tableNames.contains("table-ordering"));
    }
  }

  /** Tests for error handling. */
  @Nested
  @DisplayName("Error handling")
  class ErrorHandling {

    /** Constructs test instance. */
    ErrorHandling() {}

    /** Verifies that exception is thrown when directory doesn't exist. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when directory doesn't exist")
    void throwsException_whenDirectoryDoesNotExist() {
      // Given
      final var resolver = new TestTableOrderingResolver(".csv");
      final var nonExistentDir = Path.of("/nonexistent/directory");

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class, () -> resolver.ensureTableOrdering(nonExistentDir));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("Failed to list data files"));
    }
  }
}
