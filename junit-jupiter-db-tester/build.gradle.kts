plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.jreleaser)
    jacoco
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

java {
    withSourcesJar()
    withJavadocJar()
}

// Configure test suites using Gradle's testing DSL (Gradle 7.3+)
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            dependencies {
                implementation(platform(libs.mockito.bom))
                implementation(libs.bytebuddy)
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = "JUnit Jupiter DB Tester"
                description = "A JUnit Jupiter extension for database testing with CSV-based test data management, built on DbUnit"
                url = "https://github.com/seijikohara/junit-jupiter-db-tester"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }

                developers {
                    developer {
                        id = "seijikohara"
                        name = "Seiji Kohara"
                        email = "seiji.kohara@gmail.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/seijikohara/junit-jupiter-db-tester.git"
                    developerConnection = "scm:git:ssh://github.com/seijikohara/junit-jupiter-db-tester.git"
                    url = "https://github.com/seijikohara/junit-jupiter-db-tester"
                }
            }
        }
    }

    repositories {
        maven {
            name = "staging"
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it.name.contains("publish") }
    }
    // Use gpg-agent for signing
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

jreleaser {
    gitRootSearch = true
    // Use external YAML configuration file
    configFile = rootProject.file("jreleaser.yml")
}
