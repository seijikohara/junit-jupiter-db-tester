import com.diffplug.gradle.spotless.SpotlessExtension
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

// Force JGit version to 5.13.x for JReleaser compatibility
// JReleaser 1.21.0 requires JGit 5.13.x API (GpgObjectSigner class)
// which was removed in JGit 7.0.0 (API breaking change)
// Spotless plugin also depends on JGit but with newer version 7.4.x
buildscript {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.eclipse.jgit") {
                useVersion("5.13.3.202401111512-r")
                because("JReleaser 1.21.0 requires JGit 5.13.x API")
            }
        }
    }
}

plugins {
    // Apply plugins to subprojects using `apply false` and then apply them explicitly in subprojects
    // This approach is required because plugins block cannot be used within subprojects/allprojects blocks
    alias(libs.plugins.errorprone) apply false
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.spotless)
    // Version Catalog Update plugin for managing dependency versions
    alias(libs.plugins.version.catalog.update)
}

group = "io.github.seijikohara"

// Version is managed via gradle.properties file
// For releases, GitHub Actions workflow or command line overrides the version:
//   ./gradlew build -Pversion=1.0.0

// Configure version catalog update plugin
versionCatalogUpdate {
    // Sort version catalog entries alphabetically
    sortByKey = true
}

// Configure Spotless for root project
spotless {
    kotlinGradle {
        ktlint()
    }
}

// Configure group and version for all projects
allprojects {
    group = rootProject.group
    version = rootProject.version
}

// Java subprojects configuration (excludes BOM which uses java-platform plugin)
subprojects {
    // Skip configuration for BOM project (uses java-platform plugin instead of java)
    if (name == "junit-jupiter-db-tester-bom") {
        return@subprojects
    }

    // Apply plugins using apply() because plugins {} block cannot be used in subprojects
    // This is a Gradle limitation, not a deprecated pattern
    apply(plugin = "java")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    // Configure extensions
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    extensions.configure<TestingExtension> {
        suites {
            withType<JvmTestSuite> {
                useJUnitJupiter()
            }
        }
    }
    extensions.configure<SpotlessExtension> {
        java {
            googleJavaFormat()
        }
    }

    dependencies {
        "compileOnly"(rootProject.libs.checker.qual)
        "testCompileOnly"(rootProject.libs.checker.qual)
        "errorprone"(rootProject.libs.errorprone.annotations)
        "errorprone"(rootProject.libs.errorprone.core)
        "errorprone"(rootProject.libs.errorprone.refaster)
        "errorprone"(rootProject.libs.nullaway)
    }

    // Configure tasks
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:all",
                "-Xdoclint:all",
                "-Werror",
                "-XDaddTypeAnnotationsToSymbol=true",
            ),
        )
        options.errorprone {
            allErrorsAsWarnings = false
            disableWarningsInGeneratedCode = false
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "io.github.seijikohara.dbtester,example")
            option("NullAway:JSpecifyMode", "true")
            option("NullAway:TreatGeneratedAsUnannotated", "true")
            option("NullAway:CheckOptionalEmptiness", "true")
            option("NullAway:CheckContracts", "true")
            option("NullAway:HandleTestAssertionLibraries", "true")
        }
    }
}

// Common Maven publishing configuration for all subprojects with maven-publish plugin
subprojects {
    plugins.withType<MavenPublishPlugin> {
        configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                pom {
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

            repositories {
                maven {
                    name = "staging"
                    url =
                        layout.buildDirectory
                            .dir("staging-deploy")
                            .get()
                            .asFile
                            .toURI()
                }
            }
        }
    }

    plugins.withType<SigningPlugin> {
        configure<SigningExtension> {
            setRequired { gradle.taskGraph.allTasks.any { it.name.contains("publish") } }
            useGpgCmd()
        }
    }
}

// JReleaser configuration for Maven Central deployment
jreleaser {
    gitRootSearch = true
    configFile = file("jreleaser.yml")
}
