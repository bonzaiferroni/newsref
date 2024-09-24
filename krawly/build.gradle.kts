plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation("it.skrape:skrapeit:1.2.2")
}

application {
    mainClass.set("newsref.krawly.MainKt")
}