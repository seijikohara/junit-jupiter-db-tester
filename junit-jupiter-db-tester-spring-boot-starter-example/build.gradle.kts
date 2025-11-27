plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Spring Boot Starter
    implementation(libs.spring.boot.starter.data.jpa)

    // H2 Database
    runtimeOnly(libs.h2)

    // Test dependencies
    testImplementation(project(":junit-jupiter-db-tester-spring-boot-starter"))
    testImplementation(libs.spring.boot.starter.test)
}
