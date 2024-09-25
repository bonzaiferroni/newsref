val ktor_version: String by project

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation("it.skrape:skrapeit:1.2.2")
    implementation("com.aallam.openai:openai-client:3.8.2")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation(project(":model"))
}

application {
    mainClass.set("newsref.krawly.MainKt")
}