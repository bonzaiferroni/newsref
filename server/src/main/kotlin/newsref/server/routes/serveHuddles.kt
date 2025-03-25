package newsref.server.routes

import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import newsref.server.db.services.UserDtoService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt
import newsref.server.utilities.post

fun Routing.serveHuddles(
    service: HuddleService = HuddleService(),
    userService: UserDtoService = UserDtoService()
) {
    authenticateJwt {
        post(Api.Huddles.ReadHuddlePrompt) { sent, endpoint ->
            // val username = call.getClaim(CLAIM_USERNAME)
            // val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
            service.readPrompt(sent)
        }

        post(Api.Huddles.CreateHuddle) { sent, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
            service.createHuddle(sent, user.id)
        }
    }

}