package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kabinet.api.UserApi
import newsref.model.Api
import klutch.server.authorize
import klutch.server.UserDtoService
import klutch.server.serveUsers
import newsref.server.routes.*

fun Application.configureApiRoutes() {
    routing {
        get(Api.path) {
            call.respondText("Hello World!")
        }

        post(UserApi.Login.path) {
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