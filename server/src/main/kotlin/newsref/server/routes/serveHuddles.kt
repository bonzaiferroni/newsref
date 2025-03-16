package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import newsref.model.data.*
import newsref.server.db.services.UserDtoService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt

fun Routing.serveHuddles(
    dtoService: HuddleDtoService = HuddleDtoService(),
    seedService: HuddleSeedService = HuddleSeedService(),
    userService: UserDtoService = UserDtoService()
) {
    authenticateJwt {
        post(Api.Huddles.path) {
            val seed = call.receive<HuddleSeed>()
            val username = call.getClaim(CLAIM_USERNAME)
            val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
            val huddleId = seedService.createHuddle(seed, user.id)
            call.respond(HttpStatusCode.OK, huddleId)
        }
    }

}