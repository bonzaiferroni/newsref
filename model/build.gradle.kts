plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    js().browser()
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                api("com.eygraber:uri-kmp:0.0.18")
            }
        }
        // Add the commonTest source set
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // Add Kotlin Test library for common code
            }
        }
    }
}