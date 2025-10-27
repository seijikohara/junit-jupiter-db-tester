package io.github.seijikohara.dbtester.config;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Immutable conventions for dataset file resolution.
 *
 * <p>This record defines the naming and directory conventions used to locate CSV dataset files
 * relative to test classes and methods. The framework follows a convention-over-configuration
 * approach, using standard patterns by default while allowing customization when needed.
 *
 * <h2>Convention Components</h2>
 *
 * <ul>
 *   <li><strong>Base Directory:</strong> Optional root directory for all dataset files; when {@code
 *       null}, uses classpath-relative paths based on test class package
 *   <li><strong>Expectation Suffix:</strong> Subdirectory name for expected datasets, appended to
 *       the test class directory (default: {@code "/expected"})
 *   <li><strong>Scenario Marker:</strong> Column name that identifies scenario filtering columns in
 *       CSV files (default: {@code "[Scenario]"}); rows are filtered based on matching scenario
 *       values
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Use standard conventions
 * ConventionSettings settings = ConventionSettings.standard();
 *
 * // Customize all conventions
 * ConventionSettings custom = new ConventionSettings(
 *     "test-data",    // base directory
 *     "/expected",    // expectation suffix
 *     "[Test]"        // scenario marker
 * );
 * }</pre>
 *
 * @param baseDirectory optional base directory for dataset files; {@code null} to use
 *     classpath-relative paths
 * @param expectationSuffix subdirectory name for expected datasets
 * @param scenarioMarker column name identifying scenario filtering columns
 * @see Configuration
 */
public record ConventionSettings(
    @Nullable String baseDirectory, String expectationSuffix, String scenarioMarker) {

  /**
   * Compact constructor that validates all non-nullable record components.
   *
   * @param baseDirectory optional base directory for dataset files
   * @param expectationSuffix subdirectory name for expected datasets
   * @param scenarioMarker column name identifying scenario filtering columns
   * @throws NullPointerException if {@code expectationSuffix} or {@code scenarioMarker} is {@code
   *     null}
   */
  public ConventionSettings {
    Objects.requireNonNull(expectationSuffix, "expectationSuffix must not be null");
    Objects.requireNonNull(scenarioMarker, "scenarioMarker must not be null");
  }

  /** Default base directory: {@code null} for classpath-relative paths. */
  private static final @Nullable String DEFAULT_BASE_DIRECTORY = null;

  /** Default expectation suffix: {@code "/expected"}. */
  private static final String DEFAULT_EXPECTATION_SUFFIX = "/expected";

  /** Default scenario marker: {@code "[Scenario]"}. */
  private static final String DEFAULT_SCENARIO_MARKER = "[Scenario]";

  /**
   * Creates convention settings with standard default values.
   *
   * @return convention settings with standard defaults
   */
  public static ConventionSettings standard() {
    return new ConventionSettings(
        DEFAULT_BASE_DIRECTORY, DEFAULT_EXPECTATION_SUFFIX, DEFAULT_SCENARIO_MARKER);
  }
}
