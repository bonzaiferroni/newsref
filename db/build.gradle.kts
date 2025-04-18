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
                api(libs.exposed.core)
                implementation(libs.exposed.crypt)
                implementation(libs.exposed.dao)
                implementation(libs.exposed.jdbc)
                implementation(libs.exposed.kotlin.datetime)
                implementation(libs.exposed.json)
                implementation(libs.exposed.migration)
                implementation("com.pgvector:pgvector:0.1.6")

                implementation("org.postgresql:postgresql:42.7.3")
                implementation("org.flywaydb:flyway-core:11.7.0")
                implementation("org.flywaydb:flyway-database-postgresql:11.7.0")

                implementation(libs.logback.classic)
                api("io.github.cdimascio:dotenv-kotlin:6.4.2")

                implementation(project(":model"))
                api(project(":klutch"))
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