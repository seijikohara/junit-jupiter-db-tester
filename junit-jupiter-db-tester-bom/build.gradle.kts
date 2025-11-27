plugins {
    `java-platform`
    `maven-publish`
    signing
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":junit-jupiter-db-tester"))
        api(project(":junit-jupiter-db-tester-spring-boot-starter"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenBom") {
            from(components["javaPlatform"])

            pom {
                name = "JUnit Jupiter DB Tester BOM"
                description = "Bill of Materials for JUnit Jupiter DB Tester"
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenBom"])
}
