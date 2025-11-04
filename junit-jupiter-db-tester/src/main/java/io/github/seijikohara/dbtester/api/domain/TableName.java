package io.github.seijikohara.dbtester.api.domain;

/**
 * Type-safe wrapper for a database table name.
 *
 * @param value canonical table identifier
 */
public record TableName(String value) implements StringIdentifier<TableName> {

  /** Trims the supplied name and rejects blank identifiers. */
  public TableName {
    value = validateNonBlankString(value, "Table name");
  }
}
