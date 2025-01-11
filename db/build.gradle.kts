import java.util.*

val exposed_version: String by project
val logback_version: String by project

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-crypt:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-json:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-migration:$exposed_version")
                implementation("com.pgvector:pgvector:0.1.6")

                implementation("org.postgresql:postgresql:42.7.1")
                implementation("org.flywaydb:flyway-core:10.20.0")
                implementation("org.flywaydb:flyway-database-postgresql:10.20.0")

                implementation("ch.qos.logback:logback-classic:$logback_version")
                implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")

                implementation(project(":model"))
            }
        }

        // Define commonTest source set for shared test logic
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // Includes kotlin.test assertions and test framework
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.testcontainers:postgresql:1.20.2")
            }
        }
    }
}