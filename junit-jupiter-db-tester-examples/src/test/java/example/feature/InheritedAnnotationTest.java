package example.feature;

import io.github.seijikohara.dbtester.api.annotation.DataSet;
import io.github.seijikohara.dbtester.api.annotation.Expectation;
import io.github.seijikohara.dbtester.api.annotation.Preparation;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates annotation inheritance from a base test class.
 *
 * <p>This test class inherits:
 *
 * <ul>
 *   <li>{@code @ExtendWith(DatabaseTestExtension.class)} from {@link InheritanceTestBase}
 *   <li>Class-level {@code @Preparation} annotation from {@link InheritanceTestBase}
 *   <li>Database setup and utility methods
 * </ul>
 *
 * <p>Each test method automatically uses the base class's {@code @Preparation} unless overridden at
 * the method level.
 *
 * <p>Directory structure:
 *
 * <pre>
 * example/feature/InheritanceTestBase/
 *   TABLE1.csv          (base setup data)
 *   expected/
 *     TABLE1.csv
 * example/feature/InheritedAnnotationTest/
 *   expected/
 *     TABLE1.csv        (child class specific expectations)
 * </pre>
 */
public final class InheritedAnnotationTest extends InheritanceTestBase {

  /** Logger instance for test execution logging. */
  private static final Logger logger = LoggerFactory.getLogger(InheritedAnnotationTest.class);

  /** Creates InheritedAnnotationTest instance. */
  public InheritedAnnotationTest() {}

  /**
   * Tests using inherited class-level @Preparation annotation.
   *
   * <p>This test uses the {@code @Preparation} from {@link InheritanceTestBase} automatically.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Expectation
  void shouldUseInheritedPreparation() throws Exception {
    logger.info("Running test with inherited @Preparation");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
          INSERT INTO TABLE1 (ID, COLUMN1, COLUMN2, COLUMN3)
          VALUES (3, 'Monitor', 20, 'Warehouse B')
          """);
    }

    final var count = getRecordCount("TABLE1");
    logger.info("Record count after insert: {}", count);

    logger.info("Test with inherited @Preparation completed");
  }

  /**
   * Tests overriding inherited @Preparation with method-level annotation.
   *
   * <p>The method-level {@code @Preparation} takes precedence over the inherited class-level
   * annotation.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Preparation(dataSets = @DataSet(scenarioNames = "overrideSetup"))
  @Expectation(dataSets = @DataSet(scenarioNames = "overrideSetup"))
  void shouldOverrideInheritedPreparation() throws Exception {
    logger.info("Running test with overridden @Preparation");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate("UPDATE TABLE1 SET COLUMN2 = 50 WHERE ID = 1");
    }

    logger.info("Test with overridden @Preparation completed");
  }

  /**
   * Tests combining inherited and method-level expectations.
   *
   * <p>Uses inherited {@code @Preparation} but adds method-level {@code @Expectation}.
   *
   * @throws Exception if database operation fails
   */
  @Test
  @Expectation(dataSets = @DataSet(scenarioNames = "combinedTest"))
  void shouldCombineInheritedAndMethodLevelAnnotations() throws Exception {
    logger.info("Running test with combined annotations");

    try (final Connection connection = dataSource.getConnection();
        final var statement = connection.createStatement()) {
      statement.executeUpdate(
          """
          UPDATE TABLE1 SET COLUMN3 = 'Warehouse C' WHERE COLUMN1 = 'Laptop'
          """);
    }

    logger.info("Test with combined annotations completed");
  }
}
