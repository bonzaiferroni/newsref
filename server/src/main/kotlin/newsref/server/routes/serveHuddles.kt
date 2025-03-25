package newsref.server.routes

import io.ktor.server.routing.*
import newsref.db.services.*
import newsref.model.Api
import newsref.server.db.services.UserDtoService
import newsref.server.extensions.getClaim
import newsref.server.plugins.CLAIM_USERNAME
import newsref.server.plugins.authenticateJwt
import newsref.server.utilities.*

fun Routing.serveHuddles(
    service: HuddleService = HuddleService(),
    userService: UserDtoService = UserDtoService()
) {
    getById(Api.Huddles.GetHuddleContentById) { id, endpoint ->
        service.readHuddleContent(id)
    }

    getById(Api.Huddles.GetHuddleResponsesById) { id, endpoint ->
        service.readHuddleResponses(id)
    }

    post(Api.Huddles.ReadHuddlePrompt) { sent, endpoint ->
        service.readPrompt(sent)
    }

    authenticateJwt {
        getById(Api.Huddles.GetUserResponseId) { id, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val userId = userService.readIdByUsername(username)
            service.readUserResponseId(id, userId)
        }

        post(Api.Huddles.SubmitHuddleResponse) { sent, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val userId = userService.readIdByUsername(username)
            service.createHuddleResponse(sent, userId)
        }
    }
}