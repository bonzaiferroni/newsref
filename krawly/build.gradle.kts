val ktor_version: String by project
val kotlin_version: String by project

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation("it.skrape:skrapeit:1.2.2")

    implementation("com.microsoft.playwright:playwright:1.41.0")
    implementation("com.aallam.openai:openai-client:3.8.2")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation(project(":model"))
    implementation(project(":db"))

    implementation(libs.kotlinx.datetime)

    // Add dependencies for unit testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
}

application {
    mainClass.set("newsref.krawly.MainKt")
}

// Configure the test task to use JUnit platform
tasks.test {
    useJUnitPlatform()
}