import com.diffplug.gradle.spotless.SpotlessExtension
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    alias(libs.plugins.errorprone) apply false
    alias(libs.plugins.spotless)
}

group = "io.github.seijikohara"
version = "1.0.0-SNAPSHOT"

allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    configurations.configureEach {
        resolutionStrategy.force(rootProject.libs.guava.get())
    }

    dependencies {
        "compileOnly"(rootProject.libs.checker.qual)
        "testCompileOnly"(rootProject.libs.checker.qual)
        "errorprone"(rootProject.libs.errorprone.core)
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
            check("DeadException", CheckSeverity.ERROR)
            check("Finally", CheckSeverity.ERROR)
            check("NullableConstructor", CheckSeverity.ERROR)
            check("ReferenceEquality", CheckSeverity.ERROR)
            check("ReturnValueIgnored", CheckSeverity.ERROR)
            check("StreamResourceLeak", CheckSeverity.ERROR)
            check("FutureReturnValueIgnored", CheckSeverity.ERROR)
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
