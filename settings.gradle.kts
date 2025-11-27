plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "junit-jupiter-db-tester"

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

include(
    "junit-jupiter-db-tester",
    "junit-jupiter-db-tester-bom",
    "junit-jupiter-db-tester-examples",
    "junit-jupiter-db-tester-spring-boot-starter",
    "junit-jupiter-db-tester-spring-boot-starter-example",
)
