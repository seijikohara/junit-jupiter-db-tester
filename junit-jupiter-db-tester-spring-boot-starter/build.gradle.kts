plugins {
    `java-library`
    `maven-publish`
    signing
}

dependencies {
    // Core library
    api(project(":junit-jupiter-db-tester"))

    // Spring Boot auto-configuration
    implementation(libs.spring.boot.autoconfigure)

    // Configuration metadata generation for IDE support
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Optional: for DataSource support
    compileOnly(libs.spring.boot.starter.jdbc)

    // Optional: for testing
    compileOnly(libs.spring.boot.starter.test)
}

// Configure test suites using Gradle's testing DSL
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            dependencies {
                implementation(platform(libs.mockito.bom))
                implementation(libs.mockito.core)
                implementation(libs.mockito.junit.jupiter)
                implementation(libs.spring.test)
                implementation(libs.spring.boot.test)
                runtimeOnly(libs.slf4j.simple)
            }

            targets {
                all {
                    testTask.configure {
                        failOnNoDiscoveredTests = false
                    }
                }
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<JavaCompile>().configureEach {
        // Suppress warning about unprocessed annotations from Spring Boot
        // The spring-boot-configuration-processor only processes @ConfigurationProperties
        options.compilerArgs.add("-Xlint:-processing")
    }

    // Ensure additional-spring-configuration-metadata.json is available when annotation processor runs
    // See: https://docs.spring.io/spring-boot/specification/configuration-metadata/annotation-processor.html
    compileJava {
        inputs.files(processResources)
    }

    javadoc {
        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
            addStringOption("Xdoclint:all")
            addBooleanOption("Xwerror", true)
        }
    }

    jar {
        manifest.attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = "JUnit Jupiter DB Tester Spring Boot Starter"
                description = "Spring Boot Starter for JUnit Jupiter DB Tester"
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
