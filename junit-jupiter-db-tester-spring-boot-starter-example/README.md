# JUnit Jupiter DB Tester - Spring Boot Starter Example

This module provides working examples demonstrating the integration of JUnit Jupiter DB Tester with Spring Boot applications using Spring Data JPA.

## Project Structure

```
junit-jupiter-db-tester-spring-boot-starter-example/
├── src/main/java/example/springboot/
│   ├── ExampleApplication.java       # Spring Boot application
│   ├── User.java                     # JPA entity
│   └── UserRepository.java           # Spring Data JPA repository
├── src/main/resources/
│   └── application.properties        # Application configuration
├── src/test/java/example/springboot/
│   ├── UserRepositoryTest.java       # Basic integration test
│   └── MultipleDataSourcesTest.java  # Multiple DataSource example
├── src/test/resources/
│   ├── application.properties        # Test configuration
│   ├── schema.sql                    # Database schema
│   └── example/springboot/           # Test data (CSV files)
└── build.gradle.kts
```

## Examples

### [UserRepositoryTest.java](src/test/java/example/springboot/UserRepositoryTest.java)

Demonstrates basic Spring Boot integration with automatic DataSource registration:

- Using [`SpringBootDatabaseTestExtension`](../junit-jupiter-db-tester-spring-boot-starter/src/main/java/io/github/seijikohara/dbtester/spring/autoconfigure/SpringBootDatabaseTestExtension.java) for zero-configuration setup
- `@Preparation` annotation for loading test data
- `@Expectation` annotation for validating database state
- Convention-based CSV file resolution

**Key Features:**
- No manual `@BeforeAll` setup required
- Automatic DataSource discovery and registration
- Spring Data JPA repository testing

### [MultipleDataSourcesTest.java](src/test/java/example/springboot/MultipleDataSourcesTest.java)

Demonstrates multiple DataSource configuration:

- Defining multiple `DataSource` beans with `@Primary` annotation
- Automatic registration of all DataSources by bean name
- Default DataSource resolution based on `@Primary`
- Accessing named DataSources in test assertions

## Test Configuration

### [application.properties](src/test/resources/application.properties) (Test)

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Initialize schema before JPA
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql

# Enable database tester auto-configuration
dbtester.enabled=true
dbtester.auto-register-data-sources=true
```

## CSV Test Data

Test data files are located in [`src/test/resources/example/springboot/`](src/test/resources/example/springboot/):

### UserRepositoryTest

```
src/test/resources/example/springboot/UserRepositoryTest/
├── USERS.csv              # Preparation data
└── expected/
    └── USERS.csv          # Expected data after test
```

**USERS.csv (Preparation):**
```csv
[Scenario],ID,NAME,EMAIL
,1,Alice,alice@example.com
,2,Bob,bob@example.com
shouldSaveNewUser,1,Alice,alice@example.com
shouldSaveNewUser,2,Bob,bob@example.com
```

### MultipleDataSourcesTest

```
src/test/resources/example/springboot/MultipleDataSourcesTest/
└── USERS.csv              # Preparation data for mainDb
```

## Running the Examples

Run all example tests:

```bash
./gradlew :junit-jupiter-db-tester-spring-boot-starter-example:test
```

Run a specific test class:

```bash
./gradlew :junit-jupiter-db-tester-spring-boot-starter-example:test --tests "example.springboot.UserRepositoryTest"
./gradlew :junit-jupiter-db-tester-spring-boot-starter-example:test --tests "example.springboot.MultipleDataSourcesTest"
```

Run with detailed output:

```bash
./gradlew :junit-jupiter-db-tester-spring-boot-starter-example:test --info
```

## Dependencies

This example project uses:

- Spring Boot 4
- Spring Data JPA
- H2 Database (in-memory)
- JUnit Jupiter DB Tester Spring Boot Starter

See [`build.gradle.kts`](build.gradle.kts) for the complete dependency configuration.

## Related Modules

- [junit-jupiter-db-tester](../junit-jupiter-db-tester/) - Core library
- [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) - Bill of Materials
- [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) - Spring Boot auto-configuration
- [junit-jupiter-db-tester-examples](../junit-jupiter-db-tester-examples/) - Additional non-Spring examples
