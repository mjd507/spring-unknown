plugins {
    java
    id("org.springframework.boot") version "4.0.0-M3"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

group = "com.jiandong"
version = "0.0.1-SNAPSHOT"
description = "spring-unknown"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()

    maven { url = uri("https://repo.spring.io/snapshot") }

    maven { url = uri("https://repo.maven.apache.org/maven2/") }

}

extra["springModulithVersion"] = "2.0.0-M3"
extra["okHttpMockwebserver3"] = "5.1.0"
extra["shedlockVersion"] = "6.10.0"

dependencies {
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.integration:spring-integration-jms")
    implementation("org.springframework.integration:spring-integration-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-activemq")
    implementation("org.apache.activemq:activemq-kahadb-store")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.springframework.modulith:spring-modulith-events-jdbc") // event-publication table
    implementation("org.springframework.modulith:spring-modulith-starter-jdbc")
    implementation("org.springframework.modulith:spring-modulith-core") // application modules check
    implementation("net.javacrumbs.shedlock:shedlock-spring")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template")
    runtimeOnly("com.h2database:h2")
    // runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.squareup.okhttp3:mockwebserver3:${property("okHttpMockwebserver3")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
        mavenBom("net.javacrumbs.shedlock:shedlock-bom:${property("shedlockVersion")}")
    }
}

tasks.withType<Test> {

    testLogging.showStandardStreams = true

    useJUnitPlatform()

    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = true
        html.required = false
    }
}