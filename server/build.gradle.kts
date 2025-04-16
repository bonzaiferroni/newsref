plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
}

group = "newsref.server"
version = "0.0.1"

application {
    mainClass.set("newsref.server.ApplicationKt")
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)
    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.cors)

    implementation(libs.logback.classic)
    testImplementation(libs.kotlin.test.junit)

    implementation(project(":model"))
    implementation(project(":db"))
}