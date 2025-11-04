import com.diffplug.gradle.spotless.SpotlessExtension
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    // Apply plugins to subprojects using `apply false` and then apply them explicitly in subprojects
    // This approach is required because plugins block cannot be used within subprojects/allprojects blocks
    alias(libs.plugins.errorprone) apply false
    alias(libs.plugins.spotless)
    // Version Catalog Update plugin for managing dependency versions
    alias(libs.plugins.version.catalog.update)
}

group = "io.github.seijikohara"
version = "1.0.0-SNAPSHOT"

// Configure group and version for all projects
allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    // Apply plugins using apply() because plugins {} block cannot be used in subprojects
    // This is a Gradle limitation, not a deprecated pattern
    apply(plugin = "java")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    // Configure Java toolchain to use Java 25
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    // Force Guava version across all configurations to avoid conflicts
    configurations.configureEach {
        resolutionStrategy.force(rootProject.libs.guava.get())
    }

    dependencies {
        "compileOnly"(rootProject.libs.checker.qual)
        "testCompileOnly"(rootProject.libs.checker.qual)
        "errorprone"(rootProject.libs.errorprone.annotations)
        "errorprone"(rootProject.libs.errorprone.core)
        "errorprone"(rootProject.libs.errorprone.refaster)
        "errorprone"(rootProject.libs.nullaway)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:all",
                "-Xdoclint:all",
                "-Werror",
            ),
        )
        options.errorprone {
            allErrorsAsWarnings = false
            disableWarningsInGeneratedCode = false
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "io.github.seijikohara,example")
            option("NullAway:JSpecifyMode", "true")
            option("NullAway:AcknowledgeArrayElementNullness", "true")
            option("NullAway:TreatGeneratedAsUnannotated", "true")
            option("NullAway:CheckOptionalEmptiness", "true")
            option("NullAway:SuggestSuppressions", "false")
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
}

extensions.configure<SpotlessExtension> {
    kotlinGradle {
        ktlint()
    }
}

// Configure version catalog update plugin
versionCatalogUpdate {
    // Sort version catalog entries alphabetically
    sortByKey = true
}
