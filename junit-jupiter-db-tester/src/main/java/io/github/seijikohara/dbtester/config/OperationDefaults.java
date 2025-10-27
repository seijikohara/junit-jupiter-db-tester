package io.github.seijikohara.dbtester.config;

import io.github.seijikohara.dbtester.api.operation.Operation;
import java.util.Objects;

/**
 * Immutable default database operations for test phases.
 *
 * <p>This record defines the default operations applied during preparation and expectation phases
 * when no explicit operation is specified in test annotations. The standard defaults are {@link
 * Operation#CLEAN_INSERT} for preparation (ensuring clean state with transactional compatibility)
 * and {@link Operation#NONE} for expectations (since expectations are read-only).
 *
 * <h2>Usage Examples</h2>
 *
 * <pre>{@code
 * // Use standard defaults
 * OperationDefaults defaults = OperationDefaults.standard();
 *
 * // Customize default operations
 * OperationDefaults custom = new OperationDefaults(
 *     Operation.INSERT,    // preparation default
 *     Operation.NONE       // expectation default
 * );
 * }</pre>
 *
 * @param preparation default operation for the preparation phase
 * @param expectation default operation for the expectation phase
 * @see Operation
 * @see Configuration
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
   * Creates operation defaults with standard values.
   *
   * <p>Returns {@link Operation#CLEAN_INSERT} for preparation and {@link Operation#NONE} for
   * expectation, suitable for most testing scenarios.
   *
   * @return operation defaults with standard values
   */
  public static OperationDefaults standard() {
    return new OperationDefaults(Operation.CLEAN_INSERT, Operation.NONE);
  }
}
