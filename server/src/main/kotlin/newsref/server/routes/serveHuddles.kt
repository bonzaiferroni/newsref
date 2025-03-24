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
import newsref.server.utilities.postApi

fun Routing.serveHuddles(
    service: HuddleService = HuddleService(),
    userService: UserDtoService = UserDtoService()
) {
    postApi(Api.Huddles.Options) { sent, endpoint ->
        val prompt = service.readPrompt(sent)
        call.respond(HttpStatusCode.OK, prompt)
    }

    authenticateJwt {
//        postApi(Api.Huddles) { sent, endpoint ->
//            val username = call.getClaim(CLAIM_USERNAME)
//            println("username: $username")
//            val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
//            val huddleId = service.createHuddle(sent, user.id)
//            call.respond(HttpStatusCode.OK, huddleId)
//        }
        post(Api.Huddles.path) {
            val sent = call.receive<HuddleSeed>()
            val username = call.getClaim(CLAIM_USERNAME)
            val user = userService.findByUsernameOrEmail(username) ?: error("User not found")
            val huddleId = service.createHuddle(sent, user.id)
            call.respond(HttpStatusCode.OK, huddleId)
        }
    }

}