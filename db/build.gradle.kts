val exposedVersion: String by project
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
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")

                implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")

                implementation("ch.qos.logback:logback-classic:$logback_version")

                implementation(project(":model"))
            }
        }
    }
}