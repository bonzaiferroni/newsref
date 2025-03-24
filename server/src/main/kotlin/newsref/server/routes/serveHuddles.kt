package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import newsref.model.data.HuddleSeed
import newsref.server.db.services.UserDtoService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt
import newsref.server.utilities.postEndpoint

fun Routing.serveHuddles(
    service: HuddleService = HuddleService(),
    userService: UserDtoService = UserDtoService()
) {
    postEndpoint(Api.ReadHuddlePrompt) { sent, endpoint ->
        service.readPrompt(sent)
    }

    authenticateJwt {
        postEndpoint(Api.CreateHuddle) { sent, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
            service.createHuddle(sent, user.id)
        }
    }

}