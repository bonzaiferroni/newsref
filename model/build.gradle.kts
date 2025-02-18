@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    js().browser()
    jvm()
    wasmJs {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }
        // Add the commonTest source set
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // Add Kotlin Test library for common code
            }
        }
        val jvmTest by getting {
            dependencies {
            }
        }
    }
}