package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import newsref.model.data.*
import newsref.server.db.services.UserDtoService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt

fun Routing.serveUsers(service: UserDtoService = UserDtoService()) {

    post(Api.userEndpoint.path) {
        val info = call.receive<SignUpRequest>()
        try {
            service.createUser(info)
        } catch (e: IllegalArgumentException) {
            println("serveUsers.createUser: ${e.message}")
            call.respond(HttpStatusCode.OK, SignUpResult(false, e.message.toString()))
            return@post
        }
        call.respond(status = HttpStatusCode.OK, SignUpResult(true, "User created."))
    }

    authenticateJwt {
        get(Api.userEndpoint.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val userInfo = service.getUserInfo(username)
            call.respond(userInfo)
        }

        get(Api.privateEndpoint.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val privateInfo = service.getPrivateInfo(username)
            call.respond(privateInfo)
        }

        put(Api.userEndpoint.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val info = call.receive<EditUserRequest>()
            service.updateUser(username, info)
            call.respond(HttpStatusCode.OK, true)
        }
    }
}