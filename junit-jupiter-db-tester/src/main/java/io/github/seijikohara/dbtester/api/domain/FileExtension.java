package io.github.seijikohara.dbtester.api.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Type-safe wrapper for file extension with automatic normalization.
 *
 * <p>File extensions are normalized to lowercase using {@link Locale#ROOT} for case-insensitive
 * comparison. All extensions are stored internally with the leading dot (e.g., ".csv", ".txt").
 *
 * <h2>Normalization</h2>
 *
 * <p>Extensions are automatically normalized on construction:
 *
 * <ul>
 *   <li>Converted to lowercase
 *   <li>Leading dot is added if missing
 * </ul>
 *
 * <pre>{@code
 * new FileExtension(".CSV")  // stored as ".csv"
 * new FileExtension(".Csv")  // stored as ".csv"
 * new FileExtension(".csv")  // stored as ".csv"
 * new FileExtension("csv")   // stored as ".csv" (dot added automatically)
 * new FileExtension("CSV")   // stored as ".csv"
 * }</pre>
 *
 * <h2>Validation</h2>
 *
 * <p>After normalization, extensions must:
 *
 * <ul>
 *   <li>Have at least one character after the dot
 *   <li>Be non-null
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Creating from literal
 * FileExtension csv = new FileExtension(".csv");
 *
 * // Extracting from file name
 * Optional<FileExtension> ext = FileExtension.fromFileName("table.CSV");
 * // ext contains FileExtension(".csv")
 *
 * // Checking file name match
 * boolean matches = csv.matches("TABLE.CSV");  // true
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This record is immutable and thread-safe.
 *
 * @param value the normalized file extension (e.g., ".csv", ".txt")
 */
public record FileExtension(String value) {

  /** Compact constructor with normalization and validation. */
  public FileExtension {
    Objects.requireNonNull(value, "File extension must not be null");
    value = normalizeExtension(value);
    validateFormat(value);
  }

  /**
   * Normalizes the extension to lowercase and ensures it has a leading dot.
   *
   * <p>This method accepts extensions with or without a leading dot:
   *
   * <pre>{@code
   * normalizeExtension("csv")   // returns ".csv"
   * normalizeExtension(".csv")  // returns ".csv"
   * normalizeExtension("CSV")   // returns ".csv"
   * normalizeExtension(".CSV")  // returns ".csv"
   * }</pre>
   *
   * @param extension the extension to normalize
   * @return normalized extension (lowercase with leading dot)
   */
  private static String normalizeExtension(final String extension) {
    final var lowercaseExt = extension.toLowerCase(Locale.ROOT);
    return lowercaseExt.startsWith(".") ? lowercaseExt : String.format(".%s", lowercaseExt);
  }

  /**
   * Validates extension format.
   *
   * @param extension the extension to validate (must already be normalized)
   * @throws IllegalArgumentException if format is invalid
   */
  private static void validateFormat(final String extension) {
    if (!extension.startsWith(".")) {
      throw new IllegalArgumentException(
          String.format("File extension must start with '.': %s", extension));
    }
    if (extension.length() <= 1) {
      throw new IllegalArgumentException("File extension must not be empty after '.'");
    }
  }

  /**
   * Extracts file extension from a file name.
   *
   * <p>The extension includes the leading dot and is normalized to lowercase.
   *
   * @param fileName the file name (e.g., "table.CSV", "data.txt")
   * @return Optional containing FileExtension if present (e.g., ".csv"), empty otherwise
   */
  public static Optional<FileExtension> fromFileName(final String fileName) {
    final var dotIndex = fileName.lastIndexOf('.');
    if (dotIndex <= 0) {
      return Optional.empty();
    }
    try {
      return Optional.of(new FileExtension(fileName.substring(dotIndex)));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Checks if a file name has this extension.
   *
   * <p>The comparison is case-insensitive.
   *
   * @param fileName the file name to check (e.g., "table.CSV")
   * @return {@code true} if the file has this extension, {@code false} otherwise
   */
  public boolean matches(final String fileName) {
    return fromFileName(fileName).map(ext -> ext.equals(this)).orElse(false);
  }
}
