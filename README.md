# JUnit Jupiter DB Tester

[![Maven Central](https://img.shields.io/maven-central/v/io.github.seijikohara/junit-jupiter-db-tester)](https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester)
[![Test](https://github.com/seijikohara/junit-jupiter-db-tester/actions/workflows/test.yml/badge.svg)](https://github.com/seijikohara/junit-jupiter-db-tester/actions/workflows/test.yml)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9-02303A?logo=gradle)](https://gradle.org/)
[![JUnit](https://img.shields.io/badge/JUnit-6-25A162)](https://junit.org/)
[![DbUnit](https://img.shields.io/badge/DbUnit-3-blue)](https://dbunit.sourceforge.net/dbunit/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-2-2496ED?logo=docker)](https://testcontainers.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A [JUnit Jupiter](https://junit.org/) extension for database testing with CSV-based test data management, built on [DbUnit](https://dbunit.sourceforge.net/dbunit/). Simplify your database tests with annotation-driven data preparation and validation.

## Features

- **Convention over Configuration** - Automatic file resolution based on test class and method names
- **Declarative Testing** - Use `@Preparation` and `@Expectation` annotations to prepare and validate database state
- **Scenario-Based Testing** - Share CSV files across multiple tests using scenario filtering
- **Partial Column Validation** - Validate only the columns you care about, ignore auto-generated fields
- **Multi-Database Support** - Register and test multiple data sources simultaneously
- **Programmatic Assertions** - Advanced validation with the `DatabaseAssertion` API

## Installation

```gradle
dependencies {
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester:VERSION'
}
```

Replace `VERSION` with the latest version from [Maven Central](https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester).

## Quick Start

### 1. Register DataSource and Write Test

```java
@ExtendWith(DatabaseTestExtension.class)
final class UserServiceTest {

    @BeforeAll
    static void setupDataSource(final ExtensionContext context) {
        final DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
        // Create and configure your DataSource here (e.g., H2, MySQL, PostgreSQL)
        final DataSource dataSource = ...; // InitiaÏlize your DataSource
        registry.registerDefault(dataSource);
    }

    @Test
    @Preparation  // Loads initial data
    @Expectation  // Validates final state
    void testCreateUser() {
        userService.createUser(new User("Charlie", "charlie@example.com"));
    }
}
```

### 2. Create CSV Files

Place CSV files in `src/test/resources/[package]/[TestClass]/`:

```
src/test/resources/com/example/UserServiceTest/
  USERS.csv              # Preparation data
  expected/USERS.csv     # Expected data
```

**USERS.csv:**
```csv
[Scenario],ID,NAME,EMAIL
,1,Admin,admin@example.com
testCreateUser,2,Alice,alice@example.com
testCreateUser,3,Bob,bob@example.com
```

**expected/USERS.csv:**
```csv
[Scenario],ID,NAME,EMAIL
,1,Admin,admin@example.com
testCreateUser,2,Alice,alice@example.com
testCreateUser,3,Bob,bob@example.com
testCreateUser,4,Charlie,charlie@example.com
```

The framework automatically loads rows matching the test method name and validates the database state after test execution.

> **Note:** Rows with an empty `[Scenario]` value are treated as **common data** and included in all test scenarios. This is useful for master data or reference data shared across multiple tests.

## Core Concepts

### Annotation-Based Testing

The framework provides three core annotations for declarative database testing:

#### @Preparation

Loads CSV datasets into the database before test execution. Supports both method-level and class-level application.

```java
@Test
@Preparation  // Convention-based: loads from [TestClass]/
void testCreateUser() {
    userService.createUser(new User("Charlie", "charlie@example.com"));
}

@Test
@Preparation(dataSets = @DataSet(resourceLocation = "classpath:custom/data/"))
void testWithCustomLocation() {
    // Loads CSV files from specified location
}

@Test
@Preparation(operation = Operation.REFRESH)  // Upsert instead of clean insert
void testUpdateUser() {
    userService.updateUser(1, "Updated Name");
}
```

Default operation: `Operation.CLEAN_INSERT` (deletes existing data, then inserts test data).

#### @Expectation

Validates database state after test execution against expected CSV datasets.

```java
@Test
@Preparation
@Expectation  // Convention-based: validates against [TestClass]/expected/
void testCreateUser() {
    userService.createUser(new User("Charlie", "charlie@example.com"));
}

@Test
@Expectation(dataSets = @DataSet(resourceLocation = "classpath:expected/custom/"))
void testWithCustomExpectation() {
    // Validates against CSV files in specified location
}
```

Validation is read-only and performs row-by-row, column-by-column comparison.

**Error Messages**

When data validation fails, the framework provides clear, detailed error messages to help identify the mismatch:

```java
// Example: Value mismatch in a specific cell
org.dbunit.assertion.DbComparisonFailure[value (table=USERS, row=2, col=EMAIL)expected:<charlie@example.com> but was:<charlie@wrong.com>]

// Example: Row count mismatch
org.dbunit.assertion.DbComparisonFailure[row count (table=USERS)expected:<3> but was:<2>]
```

Each error message includes:
- **Table name** - The table where the mismatch occurred
- **Row index** - Zero-based row number (row=0 is the first data row in CSV)
- **Column name** - The specific column containing the unexpected value
- **Expected value** - The value from your CSV expectation file
- **Actual value** - The value currently in the database

Note: The framework stops at the first validation error encountered, allowing you to fix issues incrementally.

#### @DataSet

Configures CSV dataset location, data source, and scenario filtering. Used within `@Preparation` and `@Expectation`.

```java
@Test
@Preparation(dataSets = {
    @DataSet(dataSourceName = "primary"),
    @DataSet(dataSourceName = "warehouse", resourceLocation = "classpath:warehouse/data/")
})
void testMultipleDataSources() {
    // Loads data into multiple databases
}

@Test
@Preparation(dataSets = @DataSet(scenarioNames = {"scenario1", "scenario2"}))
void testMultipleScenarios() {
    // Loads rows matching either scenario from shared CSV files
}
```

### Programmatic Assertions

For advanced validation scenarios requiring fine-grained control, the framework provides the `DatabaseAssertion` API:

**Key Capabilities:**
- **Dataset Comparison** - `assertEquals(expected, actual)` for complete dataset validation
- **Partial Column Validation** - `assertEqualsIgnoreColumns(expected, actual, "TABLE_NAME", columnList)` to ignore auto-generated fields
- **Query-Based Validation** - `assertEqualsByQuery(expected, dataSource, sqlQuery, "TABLE_NAME", columnList)` for custom SQL validation

**Example Use Cases:**
- Mid-test assertions at specific execution points
- Validating results from custom SQL queries or stored procedures
- Comparing datasets with column exclusions (timestamps, auto-increment IDs)
- Dynamic assertions based on runtime conditions

See [`ProgrammaticAssertionApiTest.java`](junit-jupiter-db-tester-examples/src/test/java/example/feature/ProgrammaticAssertionApiTest.java) for complete working examples.

## Examples

The [`junit-jupiter-db-tester-examples/`](junit-jupiter-db-tester-examples/) directory contains comprehensive working examples demonstrating all features.

→ **See [junit-jupiter-db-tester-examples/README.md](junit-jupiter-db-tester-examples/README.md)** for the complete learning guide with step-by-step tutorials.

## Requirements

- Java 21 or later
- JUnit Jupiter
- JDBC-compatible database (any database supported by DbUnit)

## License

MIT License - see [LICENSE](LICENSE) file.
