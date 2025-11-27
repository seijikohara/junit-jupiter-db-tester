# JUnit Jupiter DB Tester - Spring Boot Starter

This module provides Spring Boot auto-configuration for seamless integration between JUnit Jupiter DB Tester and Spring Boot applications.

## Features

- **Auto-Configuration** - Automatic setup when Spring Boot detects the library on the classpath
- **DataSource Auto-Registration** - Automatically registers Spring-managed `DataSource` beans with the testing framework
- **Multiple DataSource Support** - Supports registration of multiple named `DataSource` beans
- **Configuration Properties** - Configurable via `application.properties` or `application.yml`

## Requirements

- Spring Boot 4 or later
- Java 21 or later

## Installation

### Gradle

```gradle
dependencies {
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester-spring-boot-starter:VERSION'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.seijikohara</groupId>
    <artifactId>junit-jupiter-db-tester-spring-boot-starter</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Replace `VERSION` with the latest version from [Maven Central](https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-spring-boot-starter).

## Usage

### Basic Usage with Automatic DataSource Registration

Use [`SpringBootDatabaseTestExtension`](src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/SpringBootDatabaseTestExtension.java) for automatic `DataSource` registration without manual setup:

```java
@SpringBootTest
@ExtendWith(SpringBootDatabaseTestExtension.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Preparation
    @Expectation
    void testCreateUser() {
        userRepository.save(new User("Alice", "alice@example.com"));
    }
}
```

### Manual Registration (Alternative)

For fine-grained control, use the base `DatabaseTestExtension` with manual registration:

```java
@SpringBootTest
@ExtendWith(DatabaseTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private DataSourceRegistrar dataSourceRegistrar;

    @BeforeAll
    void setup(ExtensionContext context) {
        DataSourceRegistry registry = DatabaseTestExtension.getRegistry(context);
        dataSourceRegistrar.registerAll(registry);
    }
}
```

## Configuration Properties

Configure the starter in `application.properties` or `application.yml`:

```properties
# Enable/disable the auto-configuration (default: true)
dbtester.enabled=true

# Enable/disable automatic DataSource registration (default: true)
dbtester.auto-register-data-sources=true
```

## Multiple DataSource Support

When multiple `DataSource` beans are configured, the starter automatically:

1. Registers all `DataSource` beans by their bean names
2. Registers the `@Primary` `DataSource` (or single `DataSource`) as the default

```java
@Configuration
class DataSourceConfig {

    @Bean
    @Primary
    DataSource mainDataSource() { ... }  // Registered as default

    @Bean
    DataSource archiveDataSource() { ... }  // Registered as "archiveDataSource"
}
```

Access named `DataSource` in tests:

```java
@Test
@Preparation(dataSets = @DataSet(dataSourceName = "archiveDataSource"))
void testWithArchiveDatabase() {
    // Uses archiveDataSource for data preparation
}
```

## Module Components

| Class | Description |
|-------|-------------|
| [`SpringBootDatabaseTestExtension`](src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/SpringBootDatabaseTestExtension.java) | JUnit extension with automatic DataSource registration |
| [`DatabaseTesterAutoConfiguration`](src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/DatabaseTesterAutoConfiguration.java) | Spring Boot auto-configuration |
| [`DataSourceRegistrar`](src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/DataSourceRegistrar.java) | Registers Spring DataSources with the testing framework |
| [`DatabaseTesterProperties`](src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/DatabaseTesterProperties.java) | Configuration properties |

## Related Modules

- [junit-jupiter-db-tester](../junit-jupiter-db-tester/) - Core library (required dependency)
- [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) - Bill of Materials
- [junit-jupiter-db-tester-examples](../junit-jupiter-db-tester-examples/) - Usage examples
- [junit-jupiter-db-tester-spring-boot-starter-example](../junit-jupiter-db-tester-spring-boot-starter-example/) - Working examples

## Documentation

For comprehensive usage examples, refer to the [example project](../junit-jupiter-db-tester-spring-boot-starter-example/).
