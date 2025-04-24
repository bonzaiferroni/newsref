package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import klutch.server.serveUsers
import newsref.server.routes.*

fun Application.configureApiRoutes() {
    routing {
        get(Api.path) {
            call.respondText("Hello World!")
        }

        serveUsers()
        serveArticles()
        serveChapters()
        serveHosts()
        serveHuddles()
        serveLogs()
    }
}