import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    `java-library`
    jacoco
    alias(libs.plugins.maven.publish)
}

dependencies {
    // Use BOM (Bill of Materials) for consistent dependency versions
    api(platform(libs.junit.bom))
    api(platform(libs.slf4j.bom))

    // API dependencies exposed to consumers
    api(libs.jspecify)
    api(libs.junit.jupiter)

    // Implementation dependencies not exposed to consumers
    implementation(libs.dbunit)
    implementation(libs.slf4j.api)
}

// Configure test suites using Gradle's testing DSL (Gradle 7.3+)
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            dependencies {
                implementation(platform(libs.mockito.bom))
                implementation(libs.mockito.core)
                implementation(libs.mockito.junit.jupiter)
            }

            targets {
                all {
                    testTask.configure {
                        finalizedBy(tasks.jacocoTestReport)
                        // Don't fail if no tests are discovered (useful for modules that might not have tests yet)
                        failOnNoDiscoveredTests = false
                    }
                }
            }
        }
    }
}

tasks {
    compileJava {
        options.compilerArgs.addAll(
            listOf(
                // Allow this module to read from the unnamed module (DbUnit and other non-modular dependencies)
                "--add-reads",
                "io.github.seijikohara.dbtester=ALL-UNNAMED",
                // Suppress warnings for automatic modules in module-info.java
                "-Xlint:-requires-automatic"
            )
        )
    }

    javadoc {
        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            charSet = "UTF-8"
            addStringOption("Xdoclint:all")
            addBooleanOption("Xwerror", true)
            addStringOption("-add-reads", "io.github.seijikohara.dbtester=ALL-UNNAMED")
        }
    }

    jar {
        manifest.attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required = true
            html.required = true
        }
    }
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true
    ))

    pom {
        name = "JUnit Jupiter DB Tester"
        description = "A JUnit Jupiter extension for database testing with CSV-based test data management, built on DbUnit"
    }
}
