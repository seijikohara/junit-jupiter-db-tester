/**
 * Module descriptor for the DbTester JUnit Jupiter extension.
 *
 * <p>The module exports the {@code io.github.seijikohara.dbtester.api} namespace, which contains
 * user-facing annotations, configuration types, dataset abstractions, and programmatic assertion
 * utilities. DbUnit remains isolated inside non-exported {@code internal.*} packages so consumers
 * do not inherit direct dependencies on DbUnit APIs.
 *
 * @since 1.0
 */
module io.github.seijikohara.dbtester {
  // Public API - always available
  requires transitive org.junit.jupiter.api;
  requires transitive java.sql;

  // Internal dependencies - not exposed to consumers
  requires org.slf4j;
  requires java.naming; // For JNDI DataSource lookup

  // Note: DbUnit is accessed from the unnamed module (classpath)
  // as it does not provide module-info.java or Automatic-Module-Name

  // Export public API packages
  exports io.github.seijikohara.dbtester.api.annotation;
  exports io.github.seijikohara.dbtester.api.assertion;
  exports io.github.seijikohara.dbtester.api.config;
  exports io.github.seijikohara.dbtester.api.context;
  exports io.github.seijikohara.dbtester.api.dataset;
  exports io.github.seijikohara.dbtester.api.domain;
  exports io.github.seijikohara.dbtester.api.exception;
  exports io.github.seijikohara.dbtester.api.extension;
  exports io.github.seijikohara.dbtester.api.loader;
  exports io.github.seijikohara.dbtester.api.operation;

  // Open extension package for JUnit Jupiter reflective access
  opens io.github.seijikohara.dbtester.api.extension to
      org.junit.platform.commons;

  // All internal packages remain encapsulated (not exported)

  uses io.github.seijikohara.dbtester.api.dataset.DataSetFormatProvider;
}
