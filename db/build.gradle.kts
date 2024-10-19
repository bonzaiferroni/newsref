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

                implementation("org.postgresql:postgresql:42.7.1")
                implementation("org.flywaydb:flyway-core:10.20.0")
                implementation("org.flywaydb:flyway-database-postgresql:10.20.0")

                implementation("ch.qos.logback:logback-classic:$logback_version")

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

// Load properties from the newsref.properties file
val properties = Properties().apply {
    file("../newsref.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

tasks.all {
    if (this is ProcessForkOptions) {
        environment("NEWSREF_ADMIN_EMAIL", properties.getProperty("NEWSREF_ADMIN_EMAIL", ""))
        environment("NEWSREF_ADMIN_NAME", properties.getProperty("NEWSREF_ADMIN_NAME", ""))
        environment("NEWSREF_ADMIN_PASSWORD", properties.getProperty("NEWSREF_ADMIN_PASSWORD", ""))
        environment("NEWSREF_ADMIN_SALT", properties.getProperty("NEWSREF_ADMIN_SALT", ""))
        environment("NEWSREF_ADMIN_USERNAME", properties.getProperty("NEWSREF_ADMIN_USERNAME", ""))
        environment("NEWSREF_APP_SECRET", properties.getProperty("NEWSREF_APP_SECRET", ""))
        environment("NEWSREF_INIT_FEED_SELECTOR", properties.getProperty("NEWSREF_INIT_FEED_SELECTOR", ""))
        environment("NEWSREF_INIT_FEED_URL", properties.getProperty("NEWSREF_INIT_FEED_URL", ""))
        environment("NEWSREF_PSQL_PW", properties.getProperty("NEWSREF_PSQL_PW", ""))
    }
}