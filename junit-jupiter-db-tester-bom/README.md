# JUnit Jupiter DB Tester - BOM (Bill of Materials)

This module provides a Bill of Materials (BOM) for managing consistent versions of JUnit Jupiter DB Tester dependencies across your project.

## Purpose

The BOM simplifies dependency management by:

- Providing a single location to manage all JUnit Jupiter DB Tester artifact versions
- Ensuring version compatibility between the core library and Spring Boot starter
- Eliminating the need to specify versions for individual artifacts

## Managed Dependencies

| Artifact | Description |
|----------|-------------|
| [`junit-jupiter-db-tester`](../junit-jupiter-db-tester/) | Core database testing library |
| [`junit-jupiter-db-tester-spring-boot-starter`](../junit-jupiter-db-tester-spring-boot-starter/) | Spring Boot auto-configuration |

## Installation

### Gradle

```gradle
dependencies {
    testImplementation platform('io.github.seijikohara:junit-jupiter-db-tester-bom:VERSION')
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester'
    // Optional: Spring Boot integration
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester-spring-boot-starter'
}
```

### Maven

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.seijikohara</groupId>
            <artifactId>junit-jupiter-db-tester-bom</artifactId>
            <version>VERSION</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.github.seijikohara</groupId>
        <artifactId>junit-jupiter-db-tester</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- Optional: Spring Boot integration -->
    <dependency>
        <groupId>io.github.seijikohara</groupId>
        <artifactId>junit-jupiter-db-tester-spring-boot-starter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Replace `VERSION` with the latest version from [Maven Central](https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-bom).

## Related Modules

- [junit-jupiter-db-tester](../junit-jupiter-db-tester/) - Core library
- [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) - Spring Boot integration
- [junit-jupiter-db-tester-examples](../junit-jupiter-db-tester-examples/) - Usage examples
- [junit-jupiter-db-tester-spring-boot-starter-example](../junit-jupiter-db-tester-spring-boot-starter-example/) - Spring Boot example project
