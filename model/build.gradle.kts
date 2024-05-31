plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    kotlin("plugin.serialization") version "1.9.22"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
}