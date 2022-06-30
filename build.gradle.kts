plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application")
    id("application")
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "com.example.micronaut.mockbean"

val micronautVersion: String by project
val targetJvmVersion: String by project

repositories {
    mavenCentral()
}

micronaut {
    version(micronautVersion)
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.micronaut.*")
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJvmVersion))
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    /**
     * Kotlin dependencies.
     */
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.2")

    /**
     * Micronaut framework dependencies.
     */
    kapt("io.micronaut.security:micronaut-security-annotations")
    kapt("io.micronaut.openapi:micronaut-openapi")

    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.reactor:micronaut-reactor")

    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")

    /**
     * Third-party dependencies.
     */
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("ch.qos.logback:logback-classic")

    /**
     * Test dependency configurations.
     */
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk")

    testImplementation("io.micronaut.reactor:micronaut-reactor-http-client")
}

application {
    mainClass.set("com.example.micronaut.Application")
}

tasks {
    test {
        systemProperty("micronaut.environments", "test")
        systemProperty("micronaut.env.deduction", false)
        dependsOn(ktlintCheck)
    }

    compileKotlin {
        kotlinOptions {
            javaParameters = true
        }
    }

    compileTestKotlin {
        kotlinOptions {
            javaParameters = true
        }
    }

    (run) {
        doFirst {
            jvmArgs = listOf("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
        }
    }
}