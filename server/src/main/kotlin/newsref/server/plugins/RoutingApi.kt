package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import klutch.server.authorize
import klutch.server.UserDtoService
import newsref.server.routes.*

fun Application.configureApiRoutes() {
    routing {
        get(Api.path) {
            call.respondText("Hello World!")
        }

        post(Api.Login.path) {
            call.authorize()
        }

        serveUsers(UserDtoService())
        serveArticles()
        serveChapters()
        serveHosts()
        serveHuddles()
        serveLogs()
    }
}