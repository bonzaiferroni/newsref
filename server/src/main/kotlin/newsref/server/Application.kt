package newsref.server

import io.ktor.server.application.*
import newsref.db.generateMigrationScript
import newsref.server.plugins.*

//val host = "https://streetlight.ing"
val host = "http://192.168.1.122:8080"

fun main(args: Array<String>) {
    if ("migrate" in args) generateMigrationScript()
    else io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureCors()
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureApiRoutes()
    configureWebSockets()
    configureLogging()
}