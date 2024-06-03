package streetlight.server

import streetlight.server.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureApiRoutes()
    configureHtmlRouting()
    configureWebSockets()
}