package newsref.server.db.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.model.Api
import newsref.model.dto.EditUserRequest
import newsref.model.dto.SignUpRequest
import newsref.model.dto.SignUpResult
import newsref.server.db.services.UserService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt

fun Routing.userRouting(service: UserService = UserService()) {

    post(Api.user.path) {
        val info = call.receive<SignUpRequest>()
        try {
            service.createUser(info)
        } catch (e: IllegalArgumentException) {
            println("userRouting.createUser: ${e.message}")
            call.respond(HttpStatusCode.OK, SignUpResult(false, e.message.toString()))
            return@post
        }
        call.respond(status = HttpStatusCode.OK, SignUpResult(true, "User created."))
    }

    authenticateJwt {
        get(Api.user.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val userInfo = service.getUserInfo(username)
            call.respond(userInfo)
        }

        get(Api.privateInfo.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val privateInfo = service.getPrivateInfo(username)
            call.respond(privateInfo)
        }

        put(Api.user.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val info = call.receive<EditUserRequest>()
            service.updateUser(username, info)
            call.respond(HttpStatusCode.OK, true)
        }
    }
}