plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.serialization)
	application
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.cbor)
	implementation("it.skrape:skrapeit:1.3.0-alpha.2")
	implementation(libs.ktor.client.apache5)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.apache)
	implementation(libs.ktor.client.encoding)
	implementation(libs.ktor.client.logging)
	implementation(libs.ktor.network.tls)
	implementation(libs.ktor.serialization.kotlinx.json)
	implementation(libs.ktor.client.content.negotiation)
	implementation("org.jline:jline:3.21.0")

	implementation("com.microsoft.playwright:playwright:1.41.0")
	implementation("com.aallam.openai:openai-client:3.8.2")
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
	implementation("com.fleeksoft.ksoup:ksoup:0.2.1")

	implementation(project(":model"))
	implementation(project(":db"))
	implementation(project(":kabinet"))
	implementation(project(":klutch"))

	implementation(libs.kotlinx.datetime)
	implementation(libs.logback.classic)

	// Add dependencies for unit testing
	testImplementation(kotlin("test"))
	testImplementation(libs.kotlin.test.junit5)
    testImplementation(project(":db"))
	testImplementation("org.testcontainers:postgresql:1.20.2")
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