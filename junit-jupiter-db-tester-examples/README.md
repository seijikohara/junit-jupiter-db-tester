# JUnit Jupiter DB Tester - Examples

This directory contains example tests demonstrating the features of the JUnit Jupiter DB Tester framework. Each test class illustrates specific functionality through executable code and associated test data in CSV format.

## Test Structure

Tests are organized into two main categories:

- **Feature Tests** (`example/feature/`) - Demonstrate framework features and capabilities
- **Database Integration Tests** (`example/database/`) - Validate compatibility with specific databases

## Feature Tests

### [MinimalExampleTest.java](src/test/java/example/feature/MinimalExampleTest.java)
Demonstrates the minimal convention-based database testing approach. Illustrates automatic CSV file resolution based on test class and method names, method-level `@Preparation` and `@Expectation` annotations, and single table operations with minimal configuration. Uses abstract table/column names (TABLE1, COLUMN1, COLUMN2) with product data (Mouse, Monitor, Keyboard).

### [ScenarioFilteringTest.java](src/test/java/example/feature/ScenarioFilteringTest.java)
Demonstrates scenario-based testing with CSV row filtering using the `[Scenario]` column marker. Illustrates sharing a single CSV file across multiple test methods, with each test loading only rows matching its method name. Demonstrates class-level `@Preparation` and `@Expectation` annotations, providing contrast with MinimalExampleTest's method-level annotations. Reduces CSV file duplication through automatic scenario filtering. Uses abstract table/column names (TABLE1, COLUMN1-3) with user data (alice, bob, charlie, etc.).

### [AnnotationConfigurationTest.java](src/test/java/example/feature/AnnotationConfigurationTest.java)
Demonstrates advanced annotation configuration including explicit `resourceLocation` specification, multiple `scenarioNames` in a single DataSet, class-level vs method-level annotation precedence, custom directory structure, and handling multiple tables with foreign key relationships.

### [ConfigurationCustomizationTest.java](src/test/java/example/feature/ConfigurationCustomizationTest.java)
Demonstrates customizing framework conventions through the Configuration API. Illustrates customization of scenario marker column name, expectation directory suffix, and other convention settings using `DatabaseTestExtension.setConfiguration()`. Applicable for adapting the framework to existing project conventions or organizational standards.

### [ComprehensiveDataTypesTest.java](src/test/java/example/feature/ComprehensiveDataTypesTest.java)
Provides comprehensive coverage of all CSV-representable H2 data types including integers (TINYINT, SMALLINT, INTEGER, BIGINT), decimals (DECIMAL, NUMERIC), floating points (REAL, FLOAT, DOUBLE), character types (CHAR, VARCHAR, CLOB, TEXT), date/time types (DATE, TIME, TIMESTAMP), booleans (BOOLEAN, BIT), binary types (BLOB with Base64 encoding), UUID values, and NULL value handling.

### [NullAndEmptyValuesTest.java](src/test/java/example/feature/NullAndEmptyValuesTest.java)
Demonstrates NULL value and empty string handling in CSV files. Illustrates using empty cells to represent SQL NULL values, distinguishing between NULL and empty string (""), handling NOT NULL constraints, and NULL values in numeric and timestamp columns.

### [OperationVariationsTest.java](src/test/java/example/feature/OperationVariationsTest.java)
Covers different database operations for test data preparation including CLEAN_INSERT (delete all then insert - default), INSERT (insert new rows), UPDATE (update existing rows), REFRESH (upsert - update if exists, insert if not), and DELETE_ALL (clear table). Each operation is demonstrated with appropriate use cases.

### [CustomExpectationPathsTest.java](src/test/java/example/feature/CustomExpectationPathsTest.java)
Demonstrates custom expectation paths for flexible test data organization. Illustrates using `@DataSet(resourceLocation = ...)` to specify custom paths for expectation data, organizing multiple expectation scenarios in subdirectories, multi-stage testing with different expected states, and complex validation with database state changes.

### [ProgrammaticAssertionApiTest.java](src/test/java/example/feature/ProgrammaticAssertionApiTest.java)
Demonstrates the programmatic `DatabaseAssertion` API for scenarios requiring more flexibility than annotations. Illustrates direct usage of assertion methods without relying solely on `@Expectation` annotation, custom SQL query validation, and combining annotation-based and programmatic approaches.

### [PartialColumnValidationTest.java](src/test/java/example/feature/PartialColumnValidationTest.java)
Demonstrates partial column validation techniques including validating only specific columns while ignoring others, excluding auto-generated columns (ID, timestamps), CSV files with subset of table columns, programmatic column exclusion, and combining partial CSV with ignore columns.

### [CustomQueryValidationTest.java](src/test/java/example/feature/CustomQueryValidationTest.java)
Demonstrates custom SQL query validation scenarios including JOIN query results, aggregation results (COUNT, SUM, AVG), filtered query results (WHERE clauses), sorted query results (ORDER BY), and complex SQL validation use cases.

### [TableOrderingStrategiesTest.java](src/test/java/example/feature/TableOrderingStrategiesTest.java)
Demonstrates table ordering strategies for foreign key constraints. Illustrates automatic alphabetical table ordering (default), manual ordering via `table-ordering.txt` file, programmatic ordering in custom directories, and handling complex table dependencies including many-to-many relationships with junction tables.

### [MultipleDataSourceTest.java](src/test/java/example/feature/MultipleDataSourceTest.java)
Demonstrates using multiple named data sources in a single test. Illustrates registering multiple named data sources, using `dataSourceName` in `@DataSet` annotations, and working with different databases simultaneously. Applicable for multi-tenant applications, microservices with separate databases, or testing data synchronization.

### [InheritedAnnotationTest.java](src/test/java/example/feature/InheritedAnnotationTest.java)
Demonstrates annotation inheritance from a base test class (`InheritanceTestBase`). Illustrates inheriting `@ExtendWith(DatabaseTestExtension.class)`, class-level `@Preparation` annotation, database setup and utility methods, and overriding inherited annotations at the method level.

### [NestedConventionTest.java](src/test/java/example/feature/NestedConventionTest.java)
Demonstrates `@Nested` test classes with convention-based data loading. Illustrates using nested classes for logical test grouping, convention-based CSV resolution for nested classes, different scenarios within nested test groups, and shared database setup across nested classes.

## Database Integration Tests

These tests validate framework compatibility with specific database systems using Testcontainers for containerized testing.

### [database/derby/DerbyIntegrationTest.java](src/test/java/example/database/derby/DerbyIntegrationTest.java)
Demonstrates Apache Derby integration for embedded database testing. Illustrates Derby-specific configuration, in-memory database setup, and integration test patterns applicable for lightweight testing scenarios without external dependencies.

### [database/hsqldb/HSQLDBIntegrationTest.java](src/test/java/example/database/hsqldb/HSQLDBIntegrationTest.java)
Demonstrates HSQLDB (HyperSQL) integration for embedded database testing. Covers HSQLDB-specific features, in-memory mode configuration, and fast integration testing patterns applicable for CI/CD pipelines requiring minimal setup.

### [database/mysql/MySQLIntegrationTest.java](src/test/java/example/database/mysql/MySQLIntegrationTest.java)
Demonstrates MySQL integration using Testcontainers for containerized database testing. Illustrates MySQL-specific data type handling, connection management, and integration test configuration applicable for CI/CD environments.

### [database/pgsql/PostgreSQLIntegrationTest.java](src/test/java/example/database/pgsql/PostgreSQLIntegrationTest.java)
Demonstrates PostgreSQL integration using Testcontainers. Covers PostgreSQL-specific features, schema management, and integration test best practices for containerized PostgreSQL instances.

### [database/oracle/OracleIntegrationTest.java](src/test/java/example/database/oracle/OracleIntegrationTest.java)
Demonstrates Oracle Database integration using Testcontainers. Illustrates Oracle-specific configuration, connection management, and integration test patterns for containerized Oracle instances. Validates Oracle compatibility as a smoke test.

### [database/mssql/MSSQLServerIntegrationTest.java](src/test/java/example/database/mssql/MSSQLServerIntegrationTest.java)
Demonstrates Microsoft SQL Server integration using Testcontainers. Illustrates SQL Server-specific configuration, connection management, and integration test patterns for containerized SQL Server instances. Validates SQL Server compatibility as a smoke test.

## Running Tests

Run all example tests:
```bash
./gradlew :example:test
```

Run all feature tests:
```bash
./gradlew :example:test --tests "example.feature.*"
```

Run all database integration tests:
```bash
./gradlew :example:test --tests "example.database.*"
```

Run a specific test class:
```bash
./gradlew :example:test --tests example.feature.MinimalExampleTest
./gradlew :example:test --tests example.feature.ScenarioFilteringTest
./gradlew :example:test --tests example.database.mysql.MySQLIntegrationTest
```

Run a specific test method:
```bash
./gradlew :example:test --tests example.feature.ScenarioFilteringTest.shouldCreateActiveUser
```

Run with detailed logging:
```bash
./gradlew :example:test --info
```

## CSV File Format

### Basic Structure
All examples use abstract naming for tables and columns:
```csv
[Scenario],ID,COLUMN1,COLUMN2
testScenario,1,Value1,100
testScenario,2,Value2,200
```

### Conventions
- **Column header**: First column `[Scenario]` is used for scenario filtering (optional)
- **Column names**: Must match database column names exactly (case-sensitive)
- **NULL values**: Use empty cells (no value between commas) to represent SQL NULL
- **Empty strings**: Use quoted empty string (`""`) to represent empty string values
- **Dates**: ISO format `2024-01-15` or `2024-01-15 10:30:00`
- **Booleans**: `TRUE`/`FALSE` or `1`/`0` (case-insensitive)
- **Commas in values**: Quote the entire value (`"Value, with comma"`)

### File Location
Expected data files are placed in the `expected/` subdirectory relative to the test data directory:
```
example/feature/MinimalExampleTest/
├── TABLE1.csv                # Preparation data
└── expected/
    └── TABLE1.csv            # Expected data

example/feature/ScenarioFilteringTest/
├── TABLE1.csv                # Preparation data
└── expected/
    └── TABLE1.csv            # Expected data

example/feature/AnnotationConfigurationTest/custom-location/
├── TABLE1.csv                # Custom location example
├── TABLE2.csv
└── expected/
    ├── TABLE1.csv
    └── TABLE2.csv
```

## Related Modules

- [junit-jupiter-db-tester](../junit-jupiter-db-tester/) - Core library
- [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) - Bill of Materials
- [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) - Spring Boot auto-configuration
- [junit-jupiter-db-tester-spring-boot-starter-example](../junit-jupiter-db-tester-spring-boot-starter-example/) - Spring Boot example project
