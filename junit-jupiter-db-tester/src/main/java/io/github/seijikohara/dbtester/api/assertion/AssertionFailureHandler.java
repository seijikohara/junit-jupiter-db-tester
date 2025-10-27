package io.github.seijikohara.dbtester.api.assertion;

/**
 * Handler for customizing assertion failure behavior during database comparisons.
 *
 * <p>This functional interface provides a framework-independent mechanism for handling database
 * assertion failures. Implementations can control how mismatches between expected and actual
 * database states are reported, logged, or accumulated.
 *
 * <h2>Use Cases</h2>
 *
 * <p>Custom handlers enable several advanced scenarios:
 *
 * <ul>
 *   <li>Collecting all failures before throwing (fail-on-last strategy)
 *   <li>Logging failures to external monitoring systems
 *   <li>Formatting failure messages for custom reporting tools
 *   <li>Integrating with specialized assertion libraries
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * AssertionFailureHandler handler = (message, expected, actual) -> {
 *   failures.add(new Failure(message, expected, actual));
 * };
 *
 * DatabaseAssertion.assertEquals(expectedDataSet, actualDataSet, handler);
 * }</pre>
 *
 * <h2>Framework Independence</h2>
 *
 * <p>This interface is part of the public API and does not expose DbUnit-specific types. The
 * framework internally adapts this interface to DbUnit's {@code FailureHandler}, ensuring that
 * client code remains decoupled from implementation details.
 *
 * @see DatabaseAssertion
 * @see io.github.seijikohara.dbtester.api.annotation.Expectation
 */
@FunctionalInterface
public interface AssertionFailureHandler {

  /**
   * Handles a comparison failure between expected and actual values.
   *
   * <p>This method is invoked for each mismatch detected during database comparison.
   * Implementations can throw an exception immediately (fail-fast strategy), accumulate failures
   * for batch reporting, or perform custom actions such as logging or metrics collection.
   *
   * @param message a descriptive failure message including context such as table name, row number,
   *     and column name
   * @param expected the expected value; may be {@code null}
   * @param actual the actual value found in the database; may be {@code null}
   */
  void handleFailure(final String message, final Object expected, final Object actual);
}
