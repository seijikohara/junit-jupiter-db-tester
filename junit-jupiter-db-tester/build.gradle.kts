plugins {
    `java-library`
    jacoco
}

dependencies {
    api(platform(libs.junit.bom))
    api(platform(libs.slf4j.bom))
    api(libs.dbunit)
    api(libs.junit.jupiter)
    api(libs.jspecify)
    implementation(libs.reflections)
    implementation(libs.slf4j.api)
}

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

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required = true
            html.required = true
        }
    }
}
