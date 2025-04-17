package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kabinet.model.EditUserRequest
import kabinet.model.SignUpRequest
import newsref.model.Api
import newsref.model.data.*
import klutch.server.UserDtoService
import newsref.server.extensions.getClaim
import klutch.server.CLAIM_USERNAME
import klutch.server.authenticateJwt
import klutch.server.get

fun Routing.serveUsers(service: UserDtoService = UserDtoService()) {

    post(Api.Users.GetUser.path) {
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
        get(Api.Users.GetUser.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val userInfo = service.readUserDto(username)
            call.respond(userInfo)
        }

        get(Api.Users.GetPrivateInfo) {
            val username = call.getClaim(CLAIM_USERNAME)
            service.getPrivateInfo(username)
        }

        put(Api.Users.GetUser.path) {
            val username = call.getClaim(CLAIM_USERNAME)
            val info = call.receive<EditUserRequest>()
            service.updateUser(username, info)
            call.respond(HttpStatusCode.OK, true)
        }
    }
}