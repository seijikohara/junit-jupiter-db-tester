package io.github.seijikohara.dbtester.api.domain;

/**
 * Value object that identifies a registered {@link javax.sql.DataSource}.
 *
 * @param value canonical data source identifier
 */
public record DataSourceName(String value) implements StringIdentifier<DataSourceName> {

  /** Validates that the name is non-null and non-blank. */
  public DataSourceName {
    value = validateNonBlankString(value, "Data source name");
  }
}
