package io.github.seijikohara.dbtester.api.domain;

/**
 * Type-safe representation of a column identifier.
 *
 * @param value canonical column identifier
 */
public record ColumnName(String value) implements StringIdentifier<ColumnName> {

  /** Trims the supplied name and rejects blank identifiers. */
  public ColumnName {
    value = validateNonBlankString(value, "Column name");
  }
}
