package io.github.seijikohara.dbtester.api.dataset;

import io.github.seijikohara.dbtester.api.domain.FileExtension;
import io.github.seijikohara.dbtester.api.domain.ScenarioMarker;
import io.github.seijikohara.dbtester.api.domain.ScenarioName;
import java.nio.file.Path;
import java.util.Collection;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;

/** SPI for constructing {@link ScenarioDataSet} instances from a particular file format. */
public interface DataSetFormatProvider {

  /**
   * Returns the file extension supported by this provider.
   *
   * <p>The extension can be specified with or without a leading dot (e.g., "csv" or ".csv", "tsv"
   * or ".tsv"). Extensions are automatically normalized to include the leading dot internally.
   *
   * <p>For consistency and simplicity, it is recommended to return extensions without the leading
   * dot.
   *
   * @return the file extension (e.g., new FileExtension("csv"), new FileExtension("tsv"))
   */
  FileExtension supportedFileExtension();

  /**
   * Creates a scenario-aware dataset from the files stored in {@code directory}.
   *
   * @param directory directory that contains one file per logical table
   * @param scenarioNames scenario filters to apply; an empty collection delegates to the caller's
   *     default
   * @param scenarioMarker logical name of the marker column used for filtering
   * @param dataSource the data source to associate with this dataset, or {@code null}
   * @return a dataset ready to be executed by the extension
   */
  ScenarioDataSet createDataSet(
      Path directory,
      Collection<ScenarioName> scenarioNames,
      ScenarioMarker scenarioMarker,
      @Nullable DataSource dataSource);
}
