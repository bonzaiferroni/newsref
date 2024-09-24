package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import newsref.model.apiPrefix
import newsref.server.db.core.authorize
import newsref.server.db.routes.dataRouting

fun Application.configureApiRoutes() {
    routing {
        get(apiPrefix) {
            call.respondText("Hello World!")
        }

        dataRouting()

        post(Api.login.path) {
            call.authorize()
        }
    }
}