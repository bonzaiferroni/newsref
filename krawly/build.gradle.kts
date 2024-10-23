import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile

val ktor_version: String by project
val kotlin_version: String by project

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation("it.skrape:skrapeit:1.3.0-alpha.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-encoding:$ktor_version")
    implementation("org.jline:jline:3.21.0")

    implementation("com.microsoft.playwright:playwright:1.41.0")
    implementation("com.aallam.openai:openai-client:3.8.2")

    implementation(project(":model"))
    implementation(project(":db"))

    implementation(libs.kotlinx.datetime)
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.11")

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

tasks.register<JavaExec>("runWithInput") {
    mainClass.set("newsref.krawly.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`  // Attach the system input to the task
    jvmArgs = listOf("-Dsun.java.command=Newsref")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "newsref.krawly.MainKt"
    }
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
