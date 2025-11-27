# JUnit Jupiter DB Tester - Core Library

This module provides the core database testing functionality for JUnit Jupiter. It offers annotation-driven data preparation and validation using CSV-based test data management, built on [DbUnit](https://dbunit.sourceforge.net/dbunit/).

## Module Overview

The core library includes:

- **JUnit Jupiter Extension** - [`DatabaseTestExtension`](src/main/java/io/github/seijikohara/dbtester/api/extension/DatabaseTestExtension.java) for test lifecycle integration
- **Annotations** - [`@Preparation`](src/main/java/io/github/seijikohara/dbtester/api/annotation/Preparation.java), [`@Expectation`](src/main/java/io/github/seijikohara/dbtester/api/annotation/Expectation.java), and [`@DataSet`](src/main/java/io/github/seijikohara/dbtester/api/annotation/DataSet.java) for declarative testing
- **Configuration API** - [`Configuration`](src/main/java/io/github/seijikohara/dbtester/api/config/Configuration.java) and [`DataSourceRegistry`](src/main/java/io/github/seijikohara/dbtester/api/config/DataSourceRegistry.java) for customization
- **Assertion API** - [`DatabaseAssertion`](src/main/java/io/github/seijikohara/dbtester/api/assertion/DatabaseAssertion.java) for programmatic validation

## Installation

### Gradle

```gradle
dependencies {
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester:VERSION'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.seijikohara</groupId>
    <artifactId>junit-jupiter-db-tester</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest version from [Maven Central](https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester).

## Basic Usage

```java
@ExtendWith(DatabaseTestExtension.class)
class UserRepositoryTest {

    @BeforeAll
    static void setupDataSource(ExtensionContext context) {
        DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
        registry.registerDefault(dataSource);
    }

    @Test
    @Preparation
    @Expectation
    void testCreateUser() {
        // Test implementation
    }
}
```

## Architecture

### Package Structure: PUBLIC API vs INTERNAL

**Fundamental Principle**: Only `api.*` packages are public-facing. All other packages are internal implementation details protected by NullAway at compile time.

```
io.github.seijikohara.dbtester
├── api/                                # PUBLIC API (user-facing)
│   ├── annotation/                     # @Preparation, @Expectation, @DataSet
│   ├── assertion/                      # DatabaseAssertion - programmatic assertions
│   ├── config/                         # Configuration, DataSourceRegistry
│   ├── context/                        # Test execution context
│   ├── dataset/                        # DataSet, Table, Row interfaces
│   ├── domain/                         # Type-safe domain value objects
│   ├── exception/                      # Exception types
│   ├── extension/                      # DatabaseTestExtension
│   ├── loader/                         # DataSetLoader interface
│   └── operation/                      # Operation enum (CLEAN_INSERT, etc.)
│
└── internal/                           # INTERNAL IMPLEMENTATION
    ├── junit/lifecycle/                # Test lifecycle orchestration
    ├── dataset/                        # Dataset implementations (CSV, etc.)
    ├── loader/                         # Convention-based loader implementation
    └── bridge/dbunit/                  # DbUnit bridge layer (complete isolation)
```

### Public API Packages

| Package | Description |
|---------|-------------|
| [`io.github.seijikohara.dbtester.api.annotation`](src/main/java/io/github/seijikohara/dbtester/api/annotation/) | Test annotations (`@Preparation`, `@Expectation`, `@DataSet`) |
| [`io.github.seijikohara.dbtester.api.extension`](src/main/java/io/github/seijikohara/dbtester/api/extension/) | JUnit Jupiter extension |
| [`io.github.seijikohara.dbtester.api.config`](src/main/java/io/github/seijikohara/dbtester/api/config/) | Configuration and data source registry |
| [`io.github.seijikohara.dbtester.api.assertion`](src/main/java/io/github/seijikohara/dbtester/api/assertion/) | Programmatic assertion API |
| [`io.github.seijikohara.dbtester.api.operation`](src/main/java/io/github/seijikohara/dbtester/api/operation/) | Database operations (CLEAN_INSERT, INSERT, etc.) |
| [`io.github.seijikohara.dbtester.api.dataset`](src/main/java/io/github/seijikohara/dbtester/api/dataset/) | Dataset abstractions |
| [`io.github.seijikohara.dbtester.api.domain`](src/main/java/io/github/seijikohara/dbtester/api/domain/) | Type-safe domain value objects |
| [`io.github.seijikohara.dbtester.api.exception`](src/main/java/io/github/seijikohara/dbtester/api/exception/) | Exception types |

### Package Dependency Rules

**Allowed Dependencies** (top-down only):
- `api.extension` may depend on: `api.*`, `internal.*`
- `api.config` may depend on: `api.domain`, `api.exception`, `api.dataset`, `api.loader`
- `api.loader` may depend on: `api.domain`, `api.exception`, `api.dataset`, `api.context`
- `api.context` may depend on: `api.config`
- `api.dataset` may depend on: `api.domain`
- `api.exception` has no dependencies on other api packages
- `api.annotation` has no dependencies on other api packages
- `api.assertion` may depend on: `api.dataset`
- `api.operation` has no dependencies on other api packages
- `internal.*` may depend on: `api.*`

### DbUnit Isolation via Bridge Pattern

The framework completely isolates DbUnit dependencies using the Bridge pattern in `internal.bridge.dbunit`. This ensures framework code remains DbUnit-independent and allows future migration to other database testing libraries.

**Key Principles**:
1. **Complete isolation**: All DbUnit dependencies consolidated in `internal.bridge.dbunit`
2. **Single entry point**: `DatabaseBridge` (Singleton) is the primary interface
3. **Type safety**: DbUnit types never leak outside bridge package hierarchy
4. **Path-based API**: Framework uses `java.nio.file.Path` throughout
5. **Exception isolation**: DbUnit exceptions wrapped in framework exceptions

### Java Platform Module System (JPMS)

**Module Name**: `io.github.seijikohara.dbtester`

- All `api.*` packages are exported and accessible to consumers
- `internal.*` packages are NOT exported and remain encapsulated
- ServiceLoader integration for auto-discovery of format providers

### Key Concepts

#### Domain Value Objects

Type-safe immutable records in `api.domain`:
- `TableName`, `ColumnName` - Database identifiers
- `ScenarioName`, `ScenarioMarker` - Test scenario identifiers
- `DataSourceName`, `SchemaName` - Data source identifiers
- `DataValue` - Cell value wrapper (supports null)
- `FileExtension` - File extension with normalization

#### Convention-Based Loading

```
src/test/resources/
  com/example/
    UserServiceTest/           # Test class name
      testCreateUser/          # Test method name (optional)
        USERS.csv              # Preparation data
        expected/              # Expectation directory
          USERS.csv            # Expected data
        table-ordering.txt     # Optional: explicit table order
```

#### Scenario Filtering

CSV files with `[Scenario]` column filter rows by test method name:
```csv
[Scenario],ID,NAME,EMAIL
testCreateUser,1,Alice,alice@example.com
testUpdateUser,1,Alice Updated,alice.updated@example.com
```

#### Operations

- **Preparation default**: `CLEAN_INSERT` (delete all rows, then insert test data)
- **Expectation default**: `NONE` (no data modification, validation only)

## Dependencies

- JUnit Jupiter (JUnit 6)
- DbUnit 3
- SLF4J API
- JSpecify (nullability annotations)

## Related Modules

- [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) - Bill of Materials for version management
- [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) - Spring Boot integration
- [junit-jupiter-db-tester-examples](../junit-jupiter-db-tester-examples/) - Comprehensive usage examples
- [junit-jupiter-db-tester-spring-boot-starter-example](../junit-jupiter-db-tester-spring-boot-starter-example/) - Spring Boot example project

## Documentation

For detailed usage documentation and examples, refer to the [main README](../README.md).
