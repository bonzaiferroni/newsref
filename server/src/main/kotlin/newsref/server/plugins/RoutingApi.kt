package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import newsref.model.apiPrefix
import newsref.server.db.authorize
import newsref.server.db.services.UserDtoService
import newsref.server.routes.*

fun Application.configureApiRoutes() {
    routing {
        get(apiPrefix) {
            call.respondText("Hello World!")
        }

        post(Api.loginEndpoint.path) {
            call.authorize()
        }

        serveUsers(UserDtoService())
        sourceRouting()
        serveChapters()
        serveHosts()
    }
}