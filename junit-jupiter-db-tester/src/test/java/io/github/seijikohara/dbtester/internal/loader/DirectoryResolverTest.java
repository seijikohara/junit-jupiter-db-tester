package io.github.seijikohara.dbtester.internal.loader;

import static org.junit.jupiter.api.Assertions.*;

import io.github.seijikohara.dbtester.exception.DataSetLoadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for {@link DirectoryResolver}. */
@DisplayName("DirectoryResolver")
class DirectoryResolverTest {

  /** Constructs test instance. */
  DirectoryResolverTest() {}

  /** Test method used for classpath resolution. */
  public void testMethod() {}

  /** Tests for resolveDirectory with classpath resources. */
  @Nested
  @DisplayName("resolveDirectory() with classpath")
  class ResolveDirectoryWithClasspath {

    /** Constructs test instance. */
    ResolveDirectoryWithClasspath() {}

    /**
     * Verifies that directory is resolved using convention-based path.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Resolves directory using convention-based path")
    void resolvesDirectory_usingConvention() throws NoSuchMethodException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);

      // When
      final var directory = resolver.resolveDirectory(null, "/testMethod");

      // Then
      assertNotNull(directory);
      assertTrue(Files.exists(directory));
      assertTrue(Files.isDirectory(directory));
    }

    /**
     * Verifies that directory is resolved with suffix.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Resolves directory with suffix")
    void resolvesDirectory_withSuffix() throws NoSuchMethodException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);

      // When
      final var directory = resolver.resolveDirectory(null, "/testMethod/expected");

      // Then
      assertNotNull(directory);
      assertTrue(Files.exists(directory));
      assertTrue(Files.isDirectory(directory));
    }

    /**
     * Verifies that directory is resolved with custom resource location.
     *
     * @throws NoSuchMethodException if test method cannot be found
     */
    @Test
    @Tag("normal")
    @DisplayName("Resolves directory with custom resource location")
    void resolvesDirectory_withCustomLocation() throws NoSuchMethodException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var customLocation =
          "classpath:io/github/seijikohara/dbtester/internal/loader/DirectoryResolverTest/testMethod";

      // When
      final var directory = resolver.resolveDirectory(customLocation, null);

      // Then
      assertNotNull(directory);
      assertTrue(Files.exists(directory));
      assertTrue(Files.isDirectory(directory));
    }

    /** Verifies that exception is thrown when directory not found. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when directory not found")
    void throwsException_whenDirectoryNotFound() {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class, () -> resolver.resolveDirectory(null, "/nonexistent"));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("Dataset directory not found"));
    }
  }

  /** Tests for resolveDirectory with file system paths. */
  @Nested
  @DisplayName("resolveDirectory() with file system")
  class ResolveDirectoryWithFileSystem {

    /** Constructs test instance. */
    ResolveDirectoryWithFileSystem() {}

    /**
     * Verifies that directory is resolved from file system path.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Resolves directory from file system path")
    void resolvesDirectory_fromFileSystemPath(@TempDir final Path tempDir) throws IOException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var csvFile = tempDir.resolve("TABLE1.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Test\n");

      // When
      final var directory = resolver.resolveDirectory(tempDir.toString(), null);

      // Then
      assertNotNull(directory);
      assertEquals(tempDir, directory);
      assertTrue(Files.exists(directory));
      assertTrue(Files.isDirectory(directory));
    }

    /** Verifies that exception is thrown when path does not exist. */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when path does not exist")
    void throwsException_whenPathDoesNotExist() {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var nonExistentPath = "/nonexistent/path";

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class, () -> resolver.resolveDirectory(nonExistentPath, null));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("Dataset directory does not exist"));
    }

    /**
     * Verifies that exception is thrown when path is not a directory.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when path is not a directory")
    void throwsException_whenPathIsNotDirectory(@TempDir final Path tempDir) throws IOException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var file = tempDir.resolve("file.txt");
      Files.writeString(file, "test");

      // When & Then
      final var exception =
          assertThrows(
              DataSetLoadException.class, () -> resolver.resolveDirectory(file.toString(), null));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("Path exists but is not a directory"));
    }
  }

  /** Tests for validateDirectoryContainsCsvFiles method. */
  @Nested
  @DisplayName("validateDirectoryContainsCsvFiles() method")
  class ValidateDirectoryContainsCsvFilesMethod {

    /** Constructs test instance. */
    ValidateDirectoryContainsCsvFilesMethod() {}

    /**
     * Verifies that validation passes when directory contains CSV files.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Validation passes when directory contains CSV files")
    void validationPasses_whenDirectoryContainsCsvFiles(@TempDir final Path tempDir)
        throws IOException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var csvFile = tempDir.resolve("TABLE1.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Test\n");

      // When & Then (should not throw)
      assertDoesNotThrow(() -> resolver.validateDirectoryContainsCsvFiles(tempDir));
    }

    /**
     * Verifies that exception is thrown when directory contains no CSV files.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("error")
    @DisplayName("Throws exception when directory contains no CSV files")
    void throwsException_whenDirectoryContainsNoCsvFiles(@TempDir final Path tempDir)
        throws IOException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var textFile = tempDir.resolve("README.txt");
      Files.writeString(textFile, "test");

      // When & Then
      final var exception =
          assertThrows(
              IllegalStateException.class,
              () -> resolver.validateDirectoryContainsCsvFiles(tempDir));
      assertNotNull(exception.getMessage());
      assertTrue(exception.getMessage().contains("contains no CSV files"));
    }

    /**
     * Verifies that validation ignores non-CSV files.
     *
     * @param tempDir temporary directory for test
     * @throws IOException if file operations fail
     */
    @Test
    @Tag("normal")
    @DisplayName("Validation ignores non-CSV files")
    void validationIgnoresNonCsvFiles(@TempDir final Path tempDir) throws IOException {
      // Given
      final var testClass = DirectoryResolverTest.class;
      final var resolver = new DirectoryResolver(testClass);
      final var csvFile = tempDir.resolve("TABLE1.csv");
      Files.writeString(csvFile, "ID,NAME\n1,Test\n");
      final var textFile = tempDir.resolve("README.txt");
      Files.writeString(textFile, "readme");
      final var orderingFile = tempDir.resolve("table-ordering.txt");
      Files.writeString(orderingFile, "TABLE1\n");

      // When & Then (should not throw)
      assertDoesNotThrow(() -> resolver.validateDirectoryContainsCsvFiles(tempDir));
    }
  }
}
