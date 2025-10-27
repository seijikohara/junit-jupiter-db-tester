testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            dependencies {
                implementation(project(":junit-jupiter-db-tester"))
                implementation(libs.jspecify)

                // Database drivers
                implementation(libs.h2)
                implementation(libs.hsqldb)
                implementation(libs.derby.client)
                implementation(libs.derby.embedded)
                implementation(libs.derby.tools)
                implementation(libs.mssql.jdbc)
                implementation(libs.mysql.connector.j)
                implementation(libs.oracle.ojdbc17)
                implementation(libs.postgresql)

                // Testing frameworks
                implementation(platform(libs.junit.bom))
                implementation(platform(libs.slf4j.bom))
                implementation(platform(libs.testcontainers.bom))
                implementation(libs.junit.jupiter)
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.testcontainers.mssqlserver)
                implementation(libs.testcontainers.mysql)
                implementation(libs.testcontainers.oracle.free)
                implementation(libs.testcontainers.postgresql)

                // Logging
                implementation(libs.slf4j.api)
                runtimeOnly(libs.slf4j.simple)
            }

            targets.all {
                testTask.configure {
                    testLogging {
                        events("passed", "skipped", "failed")
                    }
                }
            }
        }
    }
}
