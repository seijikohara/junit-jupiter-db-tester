package io.github.seijikohara.dbtester.api.config;

import io.github.seijikohara.dbtester.api.operation.Operation;
import java.util.Objects;

/**
 * Encapsulates the default {@link Operation} values applied to the preparation and expectation
 * phases.
 *
 * @param preparation default operation executed before a test runs
 * @param expectation default operation executed after a test finishes
 */
public record OperationDefaults(Operation preparation, Operation expectation) {

  /**
   * Compact constructor that validates all record components.
   *
   * @param preparation default operation for the preparation phase
   * @param expectation default operation for the expectation phase
   * @throws NullPointerException if either parameter is {@code null}
   */
  public OperationDefaults {
    Objects.requireNonNull(preparation, "preparation must not be null");
    Objects.requireNonNull(expectation, "expectation must not be null");
  }

  /**
   * Returns an instance initialised with {@link Operation#CLEAN_INSERT} and {@link Operation#NONE}.
   *
   * @return defaults using {@code CLEAN_INSERT} for preparation and {@code NONE} for verification
   */
  public static OperationDefaults standard() {
    return new OperationDefaults(Operation.CLEAN_INSERT, Operation.NONE);
  }
}
