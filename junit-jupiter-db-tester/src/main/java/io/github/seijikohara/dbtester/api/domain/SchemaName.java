package io.github.seijikohara.dbtester.api.domain;

/**
 * Type-safe representation of a database schema name.
 *
 * @param value canonical schema identifier
 */
public record SchemaName(String value) implements StringIdentifier<SchemaName> {

  /** Validates that the schema name is non-null and non-blank. */
  public SchemaName {
    value = validateNonBlankString(value, "Schema name");
  }
}
