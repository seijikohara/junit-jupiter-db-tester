package io.github.seijikohara.dbtester.internal.bridge.dbunit.assertion;

import io.github.seijikohara.dbtester.api.assertion.AssertionFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.assertion.FailureHandler;
import org.dbunit.dataset.ITable;

/**
 * Adapter that converts framework-independent {@link AssertionFailureHandler} to DbUnit's {@code
 * FailureHandler}.
 *
 * <p>This converter bridges the public API and DbUnit's assertion mechanism, isolating DbUnit
 * dependencies from the public API layer. The converter extracts row index and column name from
 * DbUnit's {@code Difference} object and formats them into human-readable messages before
 * delegating to the custom handler.
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This class is part of the DbUnit facade layer and ensures that DbUnit types never leak into
 * the public API. User code works with {@link AssertionFailureHandler}, while this converter
 * handles the conversion to DbUnit's {@link FailureHandler}.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * AssertionFailureHandler frameworkHandler = (message, expected, actual) -> {
 *   // Custom failure handling logic
 * };
 *
 * FailureHandler dbUnitHandler =
 *   FailureHandlerAdapter.toDbUnitHandler(frameworkHandler);
 *
 * // Use dbUnitHandler with DbUnit assertion APIs
 * }</pre>
 *
 * @see AssertionFailureHandler
 * @see DatabaseAssert
 */
public final class FailureHandlerAdapter {

  /**
   * Private constructor to prevent instantiation.
   *
   * <p>This class provides only static utility methods and should not be instantiated.
   *
   * @throws UnsupportedOperationException always thrown when invoked
   */
  private FailureHandlerAdapter() {
    throw new UnsupportedOperationException("This class has only static methods");
  }

  /**
   * Converts a framework-independent handler to DbUnit's {@link FailureHandler}.
   *
   * <p>The converter creates an anonymous implementation of DbUnit's {@link FailureHandler} that
   * delegates to the provided framework handler. When DbUnit reports a difference, the converter
   * extracts the row index and column name, formats them into a human-readable message, and passes
   * them to the framework handler along with the expected and actual values.
   *
   * <p><strong>Message Format:</strong> The generated message follows the format: {@code "Row %d,
   * Column %s"}, where {@code %d} is the zero-based row index from DbUnit and {@code %s} is the
   * column name from DbUnit.
   *
   * <p><strong>Error Creation:</strong> The converter provides default implementations for {@code
   * createFailure} methods that return standard JUnit {@link AssertionError} objects with formatted
   * messages.
   *
   * <p><strong>Additional Info:</strong> The converter returns an empty string for {@code
   * getAdditionalInfo}, indicating no additional context is provided by default. Subclasses of the
   * returned handler could override this method to provide custom additional information.
   *
   * @param handler the framework-independent failure handler to adapt
   * @return a DbUnit-compatible failure handler that delegates to the provided handler
   */
  public static FailureHandler toDbUnitHandler(final AssertionFailureHandler handler) {
    return new FailureHandler() {
      @Override
      public void handle(final Difference diff) {
        handler.handleFailure(
            String.format("Row %d, Column %s", diff.getRowIndex(), diff.getColumnName()),
            diff.getExpectedValue(),
            diff.getActualValue());
      }

      @Override
      public Error createFailure(final String message, final String expected, final String actual) {
        // Default JUnit assertion error
        return new AssertionError(
            String.format("%s expected:<%s> but was:<%s>", message, expected, actual));
      }

      @Override
      public Error createFailure(final String message) {
        // Default JUnit assertion error
        return new AssertionError(message);
      }

      @Override
      public String getAdditionalInfo(
          final ITable expectedTable,
          final ITable actualTable,
          final int rowIndex,
          final String columnName) {
        // No additional info by default
        return "";
      }
    };
  }
}
