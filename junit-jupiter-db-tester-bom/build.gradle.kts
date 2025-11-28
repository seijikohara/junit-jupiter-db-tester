import com.vanniktech.maven.publish.JavaPlatform

plugins {
    `java-platform`
    alias(libs.plugins.maven.publish)
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

mavenPublishing {
    configure(JavaPlatform())

    pom {
        name = "JUnit Jupiter DB Tester BOM"
        description = "Bill of Materials for JUnit Jupiter DB Tester"
    }
}
