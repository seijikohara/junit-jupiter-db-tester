/**
 * Internal implementation components (subject to change).
 *
 * <p>This package contains the framework's internal implementation:
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.junit.lifecycle}: Test lifecycle management
 *       (preparation and verification)
 *   <li>{@link io.github.seijikohara.dbtester.internal.dataset}: Database-agnostic dataset
 *       abstractions
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader}: Dataset loading and resolution
 *       strategies
 *   <li><strong>facade.dbunit:</strong> DbUnit facade layer providing unified access to DbUnit
 *       subsystems (assertion, operation, dataset loading)
 *   <li>{@link io.github.seijikohara.dbtester.internal.domain}: Domain value objects (immutable)
 * </ul>
 *
 * <p><strong>Note:</strong> Classes in this package are internal implementation details and may
 * change between versions. Use the {@code io.github.seijikohara.dbtester.api} package for stable
 * APIs.
 */
package io.github.seijikohara.dbtester.internal;
